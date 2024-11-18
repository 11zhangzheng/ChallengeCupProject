<template>
  <div class="chat-container">
    <!-- 左侧历史记录 -->
    <div class="chat-history" :class="{ 'collapsed': isHistoryCollapsed }">
      <div class="history-toggle" @click="toggleHistory">
        <i :class="isHistoryCollapsed ? 'el-icon-arrow-right' : 'el-icon-arrow-left'"></i>
      </div>
      <div class="history-content">
        <div class="history-header">
          <h3>聊天记录</h3>
        </div>
        <div class="history-list">
          <div 
            v-for="(item, index) in chatHistory" 
            :key="index"
            class="history-item"
            :class="{ active: currentChat === index }"
            @click="switchChat(index)"
          >
            <i class="el-icon-chat-dot-round"></i>
            <el-input
              v-if="item.isEditing"
              v-model="item.title"
              size="small"
              @blur="saveTitle(index)"
              @keyup.enter.native="saveTitle(index)"
            ></el-input>
            <span v-else @dblclick="editTitle(index)">{{ item.title || '新对话' }}</span>
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
                <div class="thinking-text">智农AI助手正在思考</div>
                <div class="thinking-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
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
              :disabled="isTyping"
              :show-word-limit="true"
              placeholder="请输入您的问题...(Shift + Enter 换行)"
              @keyup.enter.native="handleEnter"
              resize="none"
            ></el-input>
          </div>
          <el-button 
            type="primary" 
            :loading="isTyping"
            :disabled="!inputMessage.trim() || isTyping"
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

// 配置 markdown-it
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
    return '' // 使用默认的转义
  }
})

export default {
  name: 'ChatPage',
  data() {
    return {
      chatHistory: [
        { title: '种植技术咨询', messages: [] }
      ],
      currentChat: 0,
      inputMessage: '',
      currentMessages: [
        {
          role: 'assistant',
          content: `您好！我是智农AI助手，很高兴为您服务。我可以回答关于农作物种植的各类问题，例如：

- 水稻秧苗期管理要点有哪些？
- 玉米施肥的最佳时间是什么时候？
- 葡萄病虫害防治方法有哪些？

请问您想了解什么呢？`,
          type: 'text'
        }
      ],
      isTyping: false,
      streamController: null,
      isHistoryCollapsed: false
    }
  },
  methods: {
    handleEnter(e) {
      if (e.shiftKey) {
        // Shift + Enter 换行
        return
      }
      e.preventDefault()
      this.sendMessage()
    },

    async sendMessage() {
      if (this.isTyping || !this.inputMessage.trim()) return

      const userMessage = this.inputMessage.trim()
      this.currentMessages.push({
        role: 'user',
        content: userMessage,
        type: 'text'
      })
      
      this.inputMessage = ''
      this.scrollToBottom()

      const aiMessageIndex = this.currentMessages.length
      this.currentMessages.push({
        role: 'assistant',
        content: '',
        type: 'text'
      })

      this.isTyping = true

      try {
        const response = await fetch('/api/chat/question', {
          method: 'POST',
          headers: {
            'Content-Type': 'text/plain',
            'satoken': localStorage.getItem('satoken') || ''
          },
          body: userMessage
        })

        if (!response.ok) throw new Error('网络请求失败')

        const reader = response.body.getReader()
        const decoder = new TextDecoder()

        while (true) {
          const { done, value } = await reader.read()
          if (done) break

          const text = decoder.decode(value)
          this.currentMessages[aiMessageIndex].content += text
          this.scrollToBottom()
        }
      } catch (error) {
        console.error('API调用失败:', error)
        this.$message.error('网络请求失败，请稍后重试')
        this.currentMessages.pop()
      } finally {
        this.isTyping = false
        this.scrollToBottom()
      }
    },

    renderMarkdown(text) {
      // 使用 markdown-it 渲染，然后用 DOMPurify 理
      return DOMPurify.sanitize(md.render(text))
    },

    scrollToBottom() {
      this.$nextTick(() => {
        const container = this.$refs.messageContainer
        container.scrollTop = container.scrollHeight
      })
    },

    createNewChat() {
      this.chatHistory.push({ title: '新对话', messages: [] })
      this.currentChat = this.chatHistory.length - 1
      this.currentMessages = [{
        role: 'assistant',
        content: '您好！我是智农AI助手，很高兴为您服务。请问您有什么关于农作物种植的问题需要咨询吗？',
        type: 'text'
      }]
    },

    switchChat(index) {
      this.currentChat = index
      // 实际应用中需要加载对应的聊天记录
    },

    toggleHistory() {
      this.isHistoryCollapsed = !this.isHistoryCollapsed
    },

    editTitle(index) {
      this.$set(this.chatHistory[index], 'isEditing', true)
    },
    
    saveTitle(index) {
      this.$set(this.chatHistory[index], 'isEditing', false)
    },
    
    handleCommand(command, index) {
      if (command === 'edit') {
        this.editTitle(index)
      } else if (command === 'delete') {
        this.$confirm('确认删除该对？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.chatHistory.splice(index, 1)
          if (this.currentChat === index) {
            this.currentChat = Math.max(0, this.chatHistory.length - 1)
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
  text-align: left !important; /* 强制左对齐 */
}

.markdown-content * {
  text-align: left !important; /* 确保所有子元素也左对齐 */
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
.chat-container {
  display: flex;
  height: calc(100vh - 60px);
  background: linear-gradient(to bottom right, #FAFDF6, #F0F7ED);
  position: relative;
  overflow: hidden;
}

.chat-history {
  position: relative;
  width: 260px;
  height: 100%;
  transition: all 0.3s ease;
  background: #FFFFFF;
  z-index: 10;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(0, 0, 0, 0.1);
}

.history-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.history-header {
  padding: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
}

.history-header h3 {
  margin: 0;
  color: #4A6741;
  font-size: 16px;
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s ease;
  border-radius: 8px;
  margin: 0 8px;
  gap: 10px;
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
  flex-shrink: 0;
}

.history-item span {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
  font-size: 14px;
  margin-right: 24px;
}

.history-item .el-icon-more {
  opacity: 0;
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #666;
  transition: all 0.3s ease;
  padding: 4px;
  border-radius: 4px;
  font-size: 14px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}

.history-item:hover .el-icon-more {
  opacity: 1;
}

.history-item .el-icon-more:hover {
  background: rgba(0, 0, 0, 0.1);
}

.history-item .el-input {
  flex: 1;
  margin-right: 24px;
}

.history-item .el-input input {
  height: 28px;
  line-height: 28px;
}

.new-chat {
  padding: 15px;
  margin: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  background: #F0F7ED;
  border-radius: 8px;
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
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
  height: 100%;
  overflow: hidden; /* 防止内容溢出 */
}

.chat-messages {
  position: absolute;
  top: 0;
  bottom: 160px; /* 增加底部空间，避免被输入框遮挡 */
  left: 0;
  right: 0;
  overflow-y: auto;
  padding: 30px;
  max-width: 900px; /* 限制最大宽度 */
  margin: 0 auto; /* 水平居中 */
}

.message {
  display: flex;
  margin-bottom: 30px;
  gap: 12px;
  max-width: 80%;
}

.message.user {
  flex-direction: row-reverse;
  margin-left: auto;
  margin-right: 40px;
}

.message.assistant {
  flex-direction: row;
  margin-right: auto;
  margin-left: 40px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.user .avatar {
  background: #4A6741;
  color: white;
}

.assistant .avatar {
  background: #7BA666;
  color: white;
}

.message-content {
  padding: 16px 20px;
  border-radius: 16px;
  position: relative;
  word-break: break-word;
  max-width: 100%;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.assistant .message-content {
  background: #FFFFFF;
  border: none;
  margin-right: 0;
  margin-left: 0;
}

.user .message-content {
  background: #4A6741;
  color: white;
  margin-right: 0;
  margin-left: 0;
}

.chat-input-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px 0; /* 移除左右内边距 */
  background: transparent; /* 移除白色背景 */
  border-top: 1px solid rgba(232, 240, 229, 0.5);
}

.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 15px;
  max-width: 900px; /* 与消息区域同宽 */
  margin: 0 auto;
  padding: 15px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.input-wrapper {
  flex: 1;
  min-width: 0;
}

.el-textarea textarea {
  resize: none;
  border-radius: 12px;
  min-height: 60px;
  padding-right: 100px; /* 为按钮留出空间 */
  border: 1px solid #E8F0E5;
  transition: all 0.3s ease;
}

.el-textarea textarea:focus {
  border-color: #4A6741;
  box-shadow: 0 0 0 2px rgba(74, 103, 65, 0.1);
}

.el-button {
  position: relative; /* 改为相对定位 */
  right: auto;
  bottom: auto;
  margin-left: 10px;
  align-self: flex-end; /* 底部对齐 */
  width: 80px;
  height: 40px;
  border-radius: 20px;
  background: #4A6741;
  border: none;
  color: white;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.el-button:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 103, 65, 0.2);
  background: #3d5636;
}

.el-button:disabled {
  background: #C8D6C2;
  cursor: not-allowed;
}

/* 添加发送图标 */
.el-button i {
  font-size: 16px;
  margin-right: 4px;
}

/* 响应式设计优化 */
@media (max-width: 768px) {
  .chat-history {
    position: fixed;
    top: 60px;
    bottom: 0;
    left: 0;
    transform: translateX(-100%);
    box-shadow: none;
  }
  
  .chat-history:not(.collapsed) {
    transform: translateX(0);
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
    width: 240px; /* 移动端稍微窄一点 */
  }

  .history-toggle {
    top: 50%; /* 移动端保持在中间 */
    transform: translateY(-50%);
  }

  .chat-input-container {
    padding: 10px;
  }
  
  .chat-input {
    padding: 15px;
  }

  .message {
    max-width: 95%;
  }
  
  .message-content {
    max-width: calc(100% - 60px); /* 考虑头像和间距 */
  }
}

/* 确保滚动条样式在所有浏览器中一致 */
.chat-messages::-webkit-scrollbar,
.history-list::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb,
.history-list::-webkit-scrollbar-thumb {
  background: #C8D6C2;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-track,
.history-list::-webkit-scrollbar-track {
  background: #F0F7ED;
  border-radius: 3px;
}

/* 修复打字动画样式 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 8px;
  justify-content: flex-start; /* 确保左对齐 */
}

/* 添加滚动条样式 */
::-webkit-scrollbar {
  width: 5px;
}

::-webkit-scrollbar-thumb {
  background: rgba(74, 103, 65, 0.3);
  border-radius: 10px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

.typing-animation {
  display: flex;
  gap: 4px;
  padding: 12px;
  margin-bottom: 20px;
}

.typing-animation span {
  width: 8px;
  height: 8px;
  background: #4A6741;
  border-radius: 50%;
  animation: typing 1s infinite ease-in-out;
}

.typing-animation span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-animation span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.thinking-animation {
  padding: 8px 12px;
  background: rgba(74, 103, 65, 0.05);
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.thinking-text {
  color: #4A6741;
  font-size: 14px;
  font-weight: 500;
}

.thinking-dots {
  display: flex;
  gap: 4px;
  align-items: center;
}

.history-toggle {
  position: absolute;
  right: -15px;
  top: 20px; /* 调整到顶部 */
  transform: translateY(0);
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
  background: #3d5636;
  transform: scale(1.1);
}

.chat-history.collapsed {
  width: 50px;
}

.chat-history.collapsed .history-content {
  display: none;
}
</style> 