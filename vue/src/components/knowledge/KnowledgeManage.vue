<template>
  <div class="knowledge-manage">
    <!-- 上传区域卡片 -->
    <el-card class="upload-card" shadow="hover">
      <div class="upload-header">
        <div class="title">
          <i class="el-icon-upload"></i>
          <span>知识库文件上传</span>
        </div>
        <div class="desc">上传文件或URL将自动构建为AI问答知识库</div>
      </div>
      
      <!-- 添加URL上传区域 -->
      <div class="url-upload-section">
        <el-input
          v-model="urlInput"
          placeholder="请输入网页URL，例如: https://example.com/article"
          class="url-input">
          <template slot="prepend">URL</template>
          <el-button 
            slot="append" 
            type="primary" 
            :loading="urlLoading"
            @click="handleUrlUpload">
            {{ urlLoading ? '加载中...' : '解析网页' }}
          </el-button>
        </el-input>
      </div>

      <div class="divider">
        <span>或</span>
      </div>
      
      <!-- 原有的文件上传区域 -->
      <el-upload
        class="upload-area"
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        :on-success="uploadSuccess"
        :on-error="uploadError"
        :before-upload="beforeUpload"
        :http-request="customUpload"
        :limit="5"
        name="file"
        accept=".txt,.pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx"
        multiple>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          <div class="upload-main-text">点击或将文件拖拽到此处上传</div>
          <div class="upload-sub-text">支持 txt、pdf、doc、docx、ppt、pptx、xls、xlsx 格式</div>
        </div>
      </el-upload>
    </el-card>

    <!-- 使用指南卡片 -->
    <el-card class="guide-card" shadow="hover">
      <div class="guide-header">
        <i class="el-icon-info"></i>
        <span>使用指南</span>
      </div>
      
      <div class="guide-content">
        <div class="guide-item">
          <div class="guide-title">
            <i class="el-icon-document"></i>
            文件要求
          </div>
          <ul class="guide-list">
            <li>支持的文件格式: TXT、PDF、DOC、DOCX、PPT、PPTX、XLS、XLSX</li>
            <li>单个文件大小不超过10MB</li>
            <li>建议上传结构化的文本文件</li>
          </ul>
        </div>

        <div class="guide-item">
          <div class="guide-title">
            <i class="el-icon-warning-outline"></i>
            注意事项
          </div>
          <ul class="guide-list">
            <li>文件上传后将自动处理为知识库</li>
            <li>处理过程可能需要几分钟时间</li>
            <li>请勿重复上传相同内容的文件</li>
          </ul>
        </div>

        <div class="guide-item">
          <div class="guide-title">
            <i class="el-icon-success"></i>
            最佳实践
          </div>
          <ul class="guide-list">
            <li>建议按主题分类整理文件再上传</li>
            <li>文件内容应清晰、准确、专业</li>
            <li>定期更新知识库内容保持时效性</li>
          </ul>
        </div>
      </div>
    </el-card>

    <!-- 上传进度对话框 -->
    <el-dialog
      title="文件上传进度"
      :visible.sync="progressVisible"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      width="400px"
      center>
      <div class="progress-content">
        <div class="progress-icon">
          <i :class="progressStatus.icon"></i>
        </div>
        <div class="progress-text">{{ progressStatus.text }}</div>
        <el-progress 
          :percentage="uploadProgress"
          :status="progressStatus.type">
        </el-progress>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'KnowledgeManage',
  data() {
    return {
      uploadUrl: 'http://localhost:9999/api/chat/upload',
      urlInput: '',
      urlLoading: false,
      uploadHeaders: {
        'satoken': localStorage.getItem('satoken') || ''
      },
      progressVisible: false,
      uploadProgress: 0,
      progressStatus: {
        icon: 'el-icon-upload',
        text: '正在上传文件...',
        type: ''
      }
    }
  },
  methods: {
    customUpload(options) {
      const formData = new FormData()
      formData.append('file', options.file)
      
      console.log('开始上传文件:', {
        file: options.file,
        headers: this.uploadHeaders,
        url: this.uploadUrl
      })

      const xhr = new XMLHttpRequest()
      xhr.open('POST', this.uploadUrl, true)
      
      xhr.withCredentials = true
      
      Object.keys(this.uploadHeaders).forEach(key => {
        xhr.setRequestHeader(key, this.uploadHeaders[key])
      })
      
      xhr.onload = () => {
        if (xhr.status === 200) {
          console.log('上传响应:', xhr.responseText)
          if (xhr.responseText === '文档上传成功') {
            options.onSuccess(xhr.responseText)
          } else {
            options.onError(new Error(xhr.responseText))
          }
        } else {
          console.error('上传失败:', {
            status: xhr.status,
            response: xhr.responseText
          })
          options.onError(new Error(xhr.responseText || '上传失败'))
        }
      }
      
      xhr.onerror = () => {
        console.error('上传错误:', {
          status: xhr.status,
          statusText: xhr.statusText,
          response: xhr.responseText
        })
        options.onError(new Error('网络错误'))
      }
      
      xhr.upload.onprogress = (e) => {
        if (e.lengthComputable) {
          const percent = Math.round((e.loaded * 100) / e.total)
          this.uploadProgress = percent
          console.log('上传进度:', percent + '%')
        }
      }
      
      try {
        xhr.send(formData)
      } catch (error) {
        console.error('发送请求失败:', error)
        options.onError(new Error('发送请求失败'))
      }
    },

    beforeUpload(file) {
      console.log('准备上传文件:', {
        name: file.name,
        size: file.size,
        type: file.type
      })

      const isLt6M = file.size / 1024 / 1024 < 6
      if (!isLt6M) {
        this.$message.error('文件大小不能超过 6MB!')
        return false
      }
      
      const fileName = file.name.toLowerCase()
      const validExtensions = ['.txt', '.pdf', '.doc', '.docx', '.ppt', '.pptx', '.xls', '.xlsx']
      const isValidType = validExtensions.some(ext => fileName.endsWith(ext))
      
      if (!isValidType) {
        this.$message.error('只支持txt、pdf、doc、docx、ppt、pptx、xls、xlsx格式文件!')
        return false
      }

      this.progressVisible = true
      this.uploadProgress = 0
      this.progressStatus = {
        icon: 'el-icon-upload',
        text: '正在上传文件...',
        type: ''
      }

      return true
    },

    uploadError(err, file) {
      console.error('上传错误详情:', {
        error: err,
        response: err.response,
        status: err.status,
        file: file.name
      })
      
      let errorMessage = '上传失败'
      
      if (err.response) {
        try {
          const response = err.response
          errorMessage = (response.data && response.data.message) || response.data || '服务器处理失败'
        } catch (e) {
          console.error('解析错误响应失败:', e)
          errorMessage = '文件处���失败，请稍后重试'
        }
      }
      
      this.handleUploadError(errorMessage)
    },

    uploadSuccess(response, file) {
      console.log('上传响应:', response)
      
      const isSuccess = response === "文档上传成功" || 
                       (response && typeof response === 'object' && response.message === "文档上传成功")
      
      if (isSuccess) {
        this.uploadProgress = 100
        this.progressStatus = {
          icon: 'el-icon-success',
          text: '文件上传成功!',
          type: 'success'
        }
        
        setTimeout(() => {
          this.progressVisible = false
          this.$message.success('文件已成功上传到知识库')
        }, 1000)
      } else {
        this.handleUploadError(typeof response === 'string' ? response : '上传失败')
      }
    },

    handleUploadError(message) {
      this.progressStatus = {
        icon: 'el-icon-error',
        text: '上传失败: ' + message,
        type: 'exception'
      }
      
      setTimeout(() => {
        this.progressVisible = false
        this.$message.error(message || '上传失败，请稍后重试')
      }, 1000)
    },

    // 添加URL上传方法
    async handleUrlUpload() {
      if (!this.urlInput.trim()) {
        this.$message.warning('请输入URL')
        return
      }

      if (!this.isValidUrl(this.urlInput)) {
        this.$message.error('请输入有效的URL地址')
        return
      }

      this.urlLoading = true
      this.progressVisible = true
      this.progressStatus = {
        icon: 'el-icon-loading',
        text: '正在解析网页内容...',
        type: ''
      }

      try {
        const response = await fetch('http://localhost:9999/api/chat/url', {
          method: 'POST',
          headers: {
            'Content-Type': 'text/plain',
            ...this.uploadHeaders
          },
          body: this.urlInput
        })

        const result = await response.text()

        if (response.ok) {
          this.progressStatus = {
            icon: 'el-icon-success',
            text: '网页内容解析成功!',
            type: 'success'
          }
          this.$message.success('网页内容已成功添加到知识库')
          this.urlInput = ''
        } else {
          throw new Error(result)
        }
      } catch (error) {
        this.progressStatus = {
          icon: 'el-icon-error',
          text: '解析失败: ' + (error.message || '网络错误'),
          type: 'exception'
        }
        this.$message.error(error.message || '网页解析失败，请稍后重试')
      } finally {
        this.urlLoading = false
        setTimeout(() => {
          this.progressVisible = false
        }, 1500)
      }
    },

    isValidUrl(string) {
      try {
        new URL(string)
        return true
      } catch (_) {
        return false
      }
    }
  }
}
</script>

<style scoped>
.knowledge-manage {
  padding: 20px;
  display: flex;
  gap: 20px;
}

.upload-card {
  flex: 1;
  min-height: 400px;
}

.guide-card {
  width: 400px;
}

.upload-header {
  text-align: center;
  margin-bottom: 30px;
}

.upload-header .title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.upload-header .title i {
  margin-right: 8px;
  color: #409EFF;
}

.upload-header .desc {
  color: #909399;
  font-size: 14px;
}

.upload-area {
  display: flex;
  justify-content: center;
}

.upload-area .el-upload {
  width: 100%;
}

.upload-area .el-upload-dragger {
  width: 100%;
  height: 250px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.upload-area .el-icon-upload {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 20px;
}

.upload-main-text {
  font-size: 16px;
  margin-bottom: 10px;
}

.upload-sub-text {
  color: #909399;
  font-size: 14px;
}

.guide-header {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}

.guide-header i {
  color: #409EFF;
  margin-right: 8px;
}

.guide-item {
  margin-bottom: 25px;
}

.guide-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #303133;
}

.guide-title i {
  margin-right: 8px;
  color: #409EFF;
}

.guide-list {
  padding-left: 20px;
  margin: 0;
}

.guide-list li {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

.progress-content {
  text-align: center;
  padding: 20px;
}

.progress-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.progress-icon .el-icon-upload {
  color: #409EFF;
}

.progress-icon .el-icon-success {
  color: #67C23A;
}

.progress-icon .el-icon-error {
  color: #F56C6C;
}

.progress-text {
  margin-bottom: 20px;
  color: #606266;
}

.url-upload-section {
  margin-bottom: 20px;
  padding: 0 20px;
}

.url-input {
  width: 100%;
}

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 20px 0;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-top: 1px solid #EBEEF5;
}

.divider span {
  padding: 0 20px;
  color: #909399;
  font-size: 14px;
  background: white;
}
</style> 