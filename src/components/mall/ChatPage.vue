<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-header">
        <h2>聊天</h2>
      </div>
      <div class="chat-messages" ref="chatMessages">
        <div v-for="(message, index) in currentMessages" :key="index" class="chat-message" :class="message.role">
          <div class="message-role">{{ message.role === 'user' ? '我' : 'AI助手' }}</div>
          <div class="message-content">
            <!-- 加载动画 -->
            <div v-if="message.isLoading" class="thinking-animation">
              <div class="thinking-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
            <!-- 消息内容 -->
            <div v-else>{{ message.content }}</div>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <textarea 
          v-model="inputMessage" 
          placeholder="输入消息..." 
          @keydown.enter.prevent="sendMessage"
        ></textarea>
        <button @click="sendMessage" :disabled="isTyping">发送</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentChat: null,
      inputMessage: '',
      currentMessages: [],
      isTyping: false,
      streamingMessage: '',
      streamingInterval: null,
      streamingSpeed: 20,
    }
  },
  methods: {
    async sendMessage() {
      if (!this.currentChat || this.isTyping || !this.inputMessage.trim()) return

      const userMessage = this.inputMessage.trim()
      this.inputMessage = ''
      
      this.currentMessages.push({
        role: 'user',
        content: userMessage,
        type: 'text'
      })
      
      this.currentMessages.push({
        role: 'assistant',
        content: '',
        type: 'text',
        isLoading: true
      })
      
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
        this.streamMessage(aiResponse)
      } catch (error) {
        console.error('发送消息失败:', error)
        this.$message.error('发送消息失败')
        this.currentMessages.pop()
      }
    },
    streamMessage(message) {
      let index = 0
      const messageLength = message.length
      const lastMessageIndex = this.currentMessages.length - 1
      
      if (this.streamingInterval) {
        clearInterval(this.streamingInterval)
      }
      
      this.streamingInterval = setInterval(() => {
        if (index < messageLength) {
          this.$set(this.currentMessages[lastMessageIndex], 'content', 
            message.substring(0, index + 1))
          index++
          this.scrollToBottom()
        } else {
          clearInterval(this.streamingInterval)
          this.streamingInterval = null
          this.isTyping = false
          this.scrollToBottom()
        }
      }, this.streamingSpeed)
    },
    scrollToBottom() {
      this.$nextTick(() => {
        this.$refs.chatMessages.scrollTop = this.$refs.chatMessages.scrollHeight
      })
    },
  }
}
</script>

<style scoped>
.chat-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.chat-container {
  width: 80%;
  max-width: 600px;
  background-color: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.chat-header {
  background-color: #007bff;
  color: #fff;
  padding: 10px;
  text-align: center;
}

.chat-messages {
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 16px;
}

.chat-message {
  display: flex;
  flex-direction: column;
  max-width: 80%;
}

.chat-message.user {
  align-self: flex-end;
  align-items: flex-end;
}

.chat-message.assistant {
  align-self: flex-start;
  align-items: flex-start;
}

.message-role {
  font-weight: bold;
  color: #007bff;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  margin-top: 4px;
  min-width: 50px;
  min-height: 20px;
}

.user .message-content {
  background-color: #007bff;
  color: white;
}

.assistant .message-content {
  background-color: #f0f0f0;
  color: #333;
}

.chat-input {
  display: flex;
  padding: 10px;
}

.chat-input textarea {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  resize: none;
}

.chat-input button {
  margin-left: 10px;
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.thinking-animation {
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 50px;
}

.thinking-dots {
  display: flex;
  gap: 4px;
}

.thinking-dots span {
  width: 6px;
  height: 6px;
  background-color: #666;
  border-radius: 50%;
  display: inline-block;
  animation: thinking 1.4s infinite ease-in-out both;
}

.thinking-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.thinking-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes thinking {
  0%, 80%, 100% { 
    transform: scale(0);
  } 
  40% { 
    transform: scale(1);
  }
}

.chat-input button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style> 