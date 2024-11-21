<template>
  <div class="chat-container">
    <!-- 左侧历史记录 -->
    <div class="chat-history" :class="{ 'collapsed': isHistoryCollapsed }">
      <div class="history-toggle" @click="toggleHistory">
        <i :class="isHistoryCollapsed ? 'el-icon-arrow-right' : 'el-icon-arrow-left'"></i>
      </div>
      <div class="history-content" v-show="!isHistoryCollapsed">
        <div class="history-header">
          <h3>聊天记录</h3>
        </div>
        <div class="history-list">
          <div
            v-for="(item, index) in chatHistory"
            :key="item.id"
            class="history-item"
            :class="{ active: currentChat === item.id }"
            @click="switchChat(item.id)"
          >
            <i class="el-icon-chat-dot-round"></i>
            <el-input
              v-if="item.isEditing"
              v-model="item.title"
              size="small"
              @blur="saveTitle(index)"
              @keyup.enter.native="saveTitle(index)"
            ></el-input>
            <span v-else @dblclick="editTitle(index)">{{ item.title || '新���' }}</span>
            <el-dropdown trigger="click" @command="handleCommand($event, index)">
              <i class="el-icon-more"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="edit">重命名</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除对话</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
        <div class="new-chat" @click="createNewChat">
          <i class="el-icon-plus"></i>
          <span>新建对话</span>
        </div>
      </div>
    </div>

    <!-- 右侧聊天区域 -->
    <div class="chat-main">
      <!-- 聊天消息区域 -->
      <div class="chat-messages" ref="messageContainer">
        <template v-if="currentChat">
          <div
            v-for="(msg, index) in currentMessages"
            :key="index"
            class="message"
            :class="msg.role"
          >
            <div class="avatar">
              <i :class="msg.role === 'user' ? 'el-icon-user' : 'el-icon-service'"></i>
            </div>
            <div class="message-content">
              <div v-if="msg.role === 'user'" class="text-content">
                {{ msg.content }}
              </div>
              <div v-else>
                <div v-if="msg.content" class="markdown-content" v-html="renderMarkdown(msg.content)"></div>
                <div v-else-if="isTyping && index === currentMessages.length - 1" class="thinking-animation">
                  <div class="thinking-text">AI助手正在思考</div>
                  <div class="thinking-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <div v-else class="empty-chat">
          <i class="el-icon-chat-dot-round"></i>
          <p>请选择或创建一个对话</p>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-container">
        <div class="chat-input">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              :maxlength="1000"
              :disabled="!currentChat || isTyping"
              :show-word-limit="true"
              placeholder="请输入您的问题...(Shift + Enter 换行)"
              @keyup.enter.native="handleEnter"
            ></el-input>
          </div>
          <el-button
            type="primary"
            :loading="isTyping"
            :disabled="!currentChat || !inputMessage.trim() || isTyping"
            @click="sendMessage"
          >
            <i class="el-icon-position"></i>
            {{ isTyping ? '思考中' : '发送' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function(str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(lang, str).value
      } catch (__) {}
    }
    return ''
  }
})

export default {
  name: 'ChatPage',
  data() {
    return {
      chatHistory: [],
      currentChat: null,
      currentMessages: [],
      inputMessage: '',
      isTyping: false,
      streamingSpeed: 12,
      isHistoryCollapsed: false,
      localStorageKey: 'chat_histories'
    }
  },

  created() {
    this.loadChatHistories()
  },

  methods: {
    renderMarkdown(text) {
      return DOMPurify.sanitize(md.render(text))
    },

    handleEnter(e) {
      if (e.shiftKey) {
        return
      }
      e.preventDefault()
      this.sendMessage()
    },

    toggleHistory() {
      this.isHistoryCollapsed = !this.isHistoryCollapsed
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer
        if (container) {
          container.scrollTop = container.scrollHeight
        }
      })
    },

    loadChatHistories() {
      const histories = localStorage.getItem(this.localStorageKey)
      if (histories) {
        this.chatHistory = JSON.parse(histories)
        if (this.chatHistory.length > 0) {
          this.switchChat(this.chatHistory[0].id)
        }
      }
    },

    saveChatHistories() {
      localStorage.setItem(this.localStorageKey, JSON.stringify(this.chatHistory))
    },

    async createNewChat() {
      const newChat = {
        id: Date.now().toString(),
        title: this.generateChatTitle(),
        messages: [],
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      }

      this.chatHistory.unshift(newChat)
      this.saveChatHistories()
      await this.switchChat(newChat.id)
    },

    generateChatTitle() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const dateStr = `${year}-${month}-${day}`

      const todayChats = this.chatHistory.filter(chat => {
        return chat.title.includes(dateStr)
      })
      const chatNumber = todayChats.length + 1

      return `对话 ${dateStr} #${chatNumber}`
    },

    async switchChat(chatId) {
      const chat = this.chatHistory.find(c => c.id === chatId)
      if (chat) {
        this.currentChat = chatId
        this.currentMessages = chat.messages || []
        this.scrollToBottom()
      }
    },

    async sendMessage() {
      if (!this.currentChat || this.isTyping || !this.inputMessage.trim()) return

      const userMessage = this.inputMessage.trim()
      this.inputMessage = ''

      const userMsg = {
        role: 'user',
        content: userMessage,
        type: 'text'
      }
      this.currentMessages.push(userMsg)

      const aiMsg = {
        role: 'assistant',
        content: '',
        type: 'text',
        isLoading: true
      }
      this.currentMessages.push(aiMsg)

      const currentChat = this.chatHistory.find(c => c.id === this.currentChat)
      if (currentChat) {
        currentChat.messages = this.currentMessages
        currentChat.updateTime = new Date().toISOString()
        this.saveChatHistories()
      }

      this.scrollToBottom()
      this.isTyping = true

      try {
        const response = await fetch('/api/chat/question', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'satoken': localStorage.getItem('satoken') || ''
          },
          body: JSON.stringify({
            chatId: this.currentChat,
            question: userMessage
          })
        })

        if (!response.ok) throw new Error('网络请求失败')

        const aiResponse = await response.text()

        const lastMessage = this.currentMessages[this.currentMessages.length - 1]
        this.$set(lastMessage, 'isLoading', false)

        let displayedText = ''
        const chars = aiResponse.split('')

        const typeNextChar = () => {
          if (chars.length > 0) {
            displayedText += chars.shift()
            this.$set(lastMessage, 'content', displayedText)
            if (currentChat) {
              currentChat.messages = this.currentMessages
              this.saveChatHistories()
            }
            this.scrollToBottom()
            setTimeout(typeNextChar, this.streamingSpeed)
          } else {
            this.isTyping = false
          }
        }

        typeNextChar()
      } catch (error) {
        console.error('发送消息失败:', error)
        this.$message.error('发送消息失败')
        this.currentMessages.pop()
        this.isTyping = false
        if (currentChat) {
          currentChat.messages = this.currentMessages
          this.saveChatHistories()
        }
      }
    },

    async editTitle(index) {
      const chat = this.chatHistory[index]
      this.$set(chat, 'isEditing', true)
    },

    async saveTitle(index) {
      const chat = this.chatHistory[index]
      this.$set(chat, 'isEditing', false)
      this.saveChatHistories()
    },

    async handleCommand(command, index) {
      const chat = this.chatHistory[index]
      if (command === 'edit') {
        this.editTitle(index)
      } else if (command === 'delete') {
        this.$confirm('确认删除该对话？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.chatHistory.splice(index, 1)
          this.saveChatHistories()

          if (this.currentChat === chat.id) {
            if (this.chatHistory.length > 0) {
              this.switchChat(this.chatHistory[0].id)
            } else {
              this.currentChat = null
              this.currentMessages = []
            }
          }
        })
      }
    }
  }
}
</script>

<style>
/* Markdown样式 */
.markdown-content {
  line-height: 1.6;
  text-align: left !important;
}

.markdown-content * {
  text-align: left !important;
}

.markdown-content p {
  margin: 8px 0;
}

.markdown-content pre {
  background: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  overflow-x: auto;
}

.markdown-content code {
  font-family: 'Courier New', Courier, monospace;
  background: #f6f8fa;
  padding: 2px 4px;
  border-radius: 4px;
  font-size: 0.9em;
}

.markdown-content pre code {
  padding: 0;
  background: none;
}

.markdown-content ul, .markdown-content ol {
  padding-left: 20px;
}

.markdown-content blockquote {
  border-left: 4px solid #dfe2e5;
  margin: 0;
  padding-left: 16px;
  color: #6a737d;
}
</style>

<style scoped>
/* 主容器使用 flex 布局 */
.chat-container {
  display: flex;
  height: 100%;
  background: linear-gradient(to bottom right, #FAFDF6, #F0F7ED);
  position: relative;
}

/* 右侧主聊天区域也使用 flex 布局 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0; /* 防止 flex 子项溢出 */
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 20px 0;
}

/* 输入框容器 */
.chat-input-container {
  flex-shrink: 0;
  padding: 20px;
  background: transparent;
}

/* 输入框区域 */
.chat-input {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

/* 输入框包装器 */
.input-wrapper {
  flex: 1;
  min-width: 0;
}

/* 输入框样式 */
.input-wrapper :deep(.el-textarea__inner) {
  min-height: 44px !important;
  max-height: 200px;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #E8F0E5;
  transition: all 0.3s ease;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
}

.input-wrapper :deep(.el-textarea__inner:focus) {
  border-color: #4A6741;
  box-shadow: 0 0 0 2px rgba(74, 103, 65, 0.1);
}

/* 发送按钮 */
.el-button {
  width: 90px;
  height: 44px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s ease;
  margin-bottom: 0;
  background: #4A6741;
  border: none;
}

.el-button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 103, 65, 0.2);
  background: #3d5636;
}

.el-button:disabled {
  background: #C8D6C2;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 左侧历史记录样式 */
.chat-history {
  width: 260px;
  height: 100%;
  background: #FFFFFF;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  flex-shrink: 0;
  position: relative;
}

.chat-history.collapsed {
  width: 50px;
}

.history-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.history-header {
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.history-header h3 {
  margin: 0;
  color: #4A6741;
  font-size: 16px;
  font-weight: 600;
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.history-item:hover {
  background: rgba(74, 103, 65, 0.1);
}

.history-item.active {
  background: rgba(74, 103, 65, 0.15);
}

.history-item i.el-icon-chat-dot-round {
  font-size: 16px;
  color: #4A6741;
  margin-right: 8px;
}

.history-item span {
  flex: 1;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 24px;
}

.history-item .el-icon-more {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
  cursor: pointer;
  z-index: 2;
}

.history-item:hover .el-icon-more {
  opacity: 1;
}

.history-item .el-icon-more:hover {
  background: rgba(0, 0, 0, 0.1);
}

.new-chat {
  margin: 10px;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
  background: #F0F7ED;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.new-chat:hover {
  background: #E8F0E5;
}

.new-chat i {
  font-size: 16px;
  color: #4A6741;
}

.new-chat span {
  color: #4A6741;
  font-size: 14px;
  font-weight: 500;
}

.history-toggle {
  position: absolute;
  right: -15px;
  top: 20px;
  width: 30px;
  height: 30px;
  background: #4A6741;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  z-index: 11;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.history-toggle:hover {
  transform: scale(1.1);
  background: #3d5636;
}

/* 输入框样式优化 */
.history-item .el-input {
  margin-right: 24px;
}

.history-item .el-input input {
  height: 28px;
  border-radius: 4px;
  border: 1px solid #E8F0E5;
}

.history-item .el-input input:focus {
  border-color: #4A6741;
  box-shadow: 0 0 0 2px rgba(74, 103, 65, 0.1);
}

/* 响应式设计补充 */
@media (max-width: 768px) {
  .chat-history {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
    transform: translateX(-100%);
  }

  .chat-history:not(.collapsed) {
    transform: translateX(0);
  }

  .history-toggle {
    right: -15px;
  }
}

/* 消息气泡样式优化 */
.message {
  display: flex;
  margin-bottom: 24px;
  gap: 12px;
  max-width: 85%;
}

.message.user {
  flex-direction: row-reverse;
  margin-left: auto;
}

.message.assistant {
  margin-right: auto;
}

.message-content {
  padding: 16px 20px;
  border-radius: 16px;
  position: relative;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.user .message-content {
  background: #4A6741;
  color: white;
  border-bottom-right-radius: 4px;
}

.assistant .message-content {
  background: white;
  border-bottom-left-radius: 4px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-input-container {
    padding: 12px;
  }

  .chat-input {
    padding: 12px;
  }

  .message {
    max-width: 90%;
  }

  .el-button {
    width: 80px;
  }
}

/* 修改历史记录收起相关样式 */
.chat-history.collapsed .history-content {
  display: none;
}

/* 修改设置按钮样式 */
.history-item {
  padding-right: 36px;
}

.history-item .el-icon-more {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0;
  color: #666;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
  cursor: pointer;
  z-index: 2;
}
</style>
