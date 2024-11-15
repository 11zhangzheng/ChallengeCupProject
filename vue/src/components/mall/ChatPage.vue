<template>
  <div class="chat-container">
    <!-- 左侧历史记录 -->
    <div class="chat-history">
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
          <span>{{ item.title || '新对话' }}</span>
        </div>
      </div>
      <div class="new-chat" @click="createNewChat">
        <i class="el-icon-plus"></i>
        <span>新建对话</span>
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
            <!-- 如果是图片消息 -->
            <img v-if="msg.type === 'image'" :src="msg.content" class="message-image"/>
            <!-- 如果是文本消息 -->
            <span v-else>{{ msg.content }}</span>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input">
        <div class="input-wrapper">
          <div class="input-tools">
            <el-upload
              class="image-upload"
              action="#"
              :show-file-list="false"
              :before-upload="handleImageUpload"
            >
              <i class="el-icon-picture-outline tool-icon"></i>
            </el-upload>
          </div>
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入您的问题..."
            @keyup.enter.native="sendMessage"
          ></el-input>
        </div>
        <el-button 
          type="primary" 
          :disabled="!inputMessage.trim() && !imageFile"
          @click="sendMessage"
        >发送</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatPage',
  data() {
    return {
      chatHistory: [
        { title: '种植技术咨询', messages: [] }
      ],
      currentChat: 0,
      inputMessage: '',
      imageFile: null,
      currentMessages: [
        {
          role: 'assistant',
          content: '您好！我是智农AI助手，很高兴为您服务。请问您有什么关于农作物种植的问题需要咨询吗？',
          type: 'text'
        }
      ],
      isStreaming: false
    }
  },
  methods: {
    async handleImageUpload(file) {
      // 检查文件类型和大小
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }

      // 转换图片为 base64
      const reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.imageFile = reader.result
        // 直接发送图片消息
        this.sendMessage(true)
      }
      return false // 阻止自动上传
    },

    async sendMessage(isImage = false) {
      if (this.isStreaming) return
      if (!isImage && !this.inputMessage.trim() && !this.imageFile) return

      // 添加用户消息
      if (isImage && this.imageFile) {
        this.currentMessages.push({
          role: 'user',
          content: this.imageFile,
          type: 'image'
        })
        this.imageFile = null
      } else {
        this.currentMessages.push({
          role: 'user',
          content: this.inputMessage,
          type: 'text'
        })
      }

      const userInput = this.inputMessage
      this.inputMessage = ''
      this.scrollToBottom()

      // 模拟流式回复
      this.isStreaming = true
      const response = '这是一个模拟的AI回复，实际开发时需要对接后端API。我们可以看到流式输出的效果。'
      let streamedResponse = ''
      
      // 创建空的回复消息
      this.currentMessages.push({
        role: 'assistant',
        content: '',
        type: 'text'
      })

      // 快速流式输出
      for (let char of response) {
        streamedResponse += char
        this.currentMessages[this.currentMessages.length - 1].content = streamedResponse
        await new Promise(resolve => setTimeout(resolve, 20)) // 每字符 20ms
        this.scrollToBottom()
      }

      this.isStreaming = false
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
    }
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 60px);
  background: #FAFDF6;
  overflow: hidden;
}

.chat-history {
  width: 260px;
  border-right: 1px solid #E8F0E5;
  display: flex;
  flex-direction: column;
  background: #FFFFFF;
}

.history-header {
  padding: 20px;
  border-bottom: 1px solid #E8F0E5;
  color: #2C5530;
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.history-item {
  padding: 12px 15px;
  margin: 5px 0;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4A6741;
  transition: all 0.3s ease;
}

.history-item:hover {
  background: #F0F7ED;
}

.history-item.active {
  background: #E8F0E5;
  color: #2C5530;
}

.new-chat {
  padding: 15px;
  margin: 10px;
  border-radius: 8px;
  background: #F0F7ED;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #4A6741;
  transition: all 0.3s ease;
}

.new-chat:hover {
  background: #E8F0E5;
  color: #2C5530;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #FAFDF6;
  min-height: 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  min-height: 200px;
}

.message {
  display: flex;
  margin-bottom: 20px;
  gap: 12px;
  max-width: 85%;
}

.message.user {
  flex-direction: row-reverse;
  margin-left: auto;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
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
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  font-size: 14px;
  position: relative;
  word-break: break-word;
  max-width: 100%;
}

.assistant .message-content {
  background: white;
  margin-right: 40px;
  border: 1px solid #E8F0E5;
  color: #2C5530;
}

.assistant .message-content::before {
  content: '';
  position: absolute;
  left: -8px;
  top: 15px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid white;
  z-index: 1;
}

.assistant .message-content::after {
  content: '';
  position: absolute;
  left: -9px;
  top: 15px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid #E8F0E5;
}

.user .message-content {
  background: #4A6741;
  color: white;
  margin-left: 40px;
}

.user .message-content::before {
  content: '';
  position: absolute;
  right: -8px;
  top: 15px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-left: 8px solid #4A6741;
}

.chat-input {
  padding: 20px;
  background: white;
  border-top: 1px solid #E8F0E5;
  box-shadow: 0 -2px 10px rgba(232, 240, 229, 0.5);
  flex-shrink: 0;
}

.input-wrapper {
  max-height: 150px;
  margin-bottom: 10px;
  display: flex;
  align-items: flex-start;
  background: #FAFDF6;
  border-radius: 8px;
  border: 1px solid #E8F0E5;
}

.input-tools {
  position: relative;
  padding: 10px;
  display: flex;
  align-items: center;
}

.tool-icon {
  font-size: 20px;
  color: #4A6741;
  cursor: pointer;
  transition: all 0.3s;
  padding: 5px;
  border-radius: 4px;
}

.tool-icon:hover {
  color: #2C5530;
  background: #E8F0E5;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-input .el-textarea textarea {
  max-height: 100px;
  resize: none;
  border: none;
  background: transparent;
  color: #2C5530;
}

.chat-input .el-textarea textarea:focus {
  box-shadow: none;
}

.chat-input .el-button {
  float: right;
  background: #4A6741;
  border-color: #4A6741;
  padding: 12px 24px;
  transition: all 0.3s ease;
}

.chat-input .el-button:hover:not(:disabled) {
  background: #2C5530;
  border-color: #2C5530;
}

.chat-input .el-button:disabled {
  background: #E8F0E5;
  border-color: #E8F0E5;
}

.message-image {
  max-width: 300px;
  max-height: 300px;
  border-radius: 12px;
  border: 2px solid #E8F0E5;
}

/* 定义滚动条 */
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
</style> 