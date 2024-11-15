<template>
  <div class="knowledge-manage">
    <!-- 上传区域卡片 -->
    <el-card class="upload-card" shadow="hover">
      <div class="upload-header">
        <div class="title">
          <i class="el-icon-upload"></i>
          <span>知识库文件上传</span>
        </div>
        <div class="desc">上传文件将自动构建为AI问答知识库</div>
      </div>
      
      <!-- 文档上传的接口在这里 -->
      <el-upload
        class="upload-area"
        drag
        action="/api/knowledge/upload"
        :headers="uploadHeaders"
        :on-success="uploadSuccess"
        :on-error="uploadError"
        :before-upload="beforeUpload"
        :limit="5"
        multiple>
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">
          <div class="upload-main-text">点击或将文件拖拽到此处上传</div>
          <div class="upload-sub-text">支持 txt、pdf、doc、docx 格式</div>
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
            <li>支持的文件格式: TXT、PDF、DOC、DOCX</li>
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
      uploadHeaders: {
        'Authorization': this.$store.state.token
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
    // 上传前校验
    beforeUpload(file) {
      const isValidType = ['text/plain', 'application/pdf', 'application/msword', 
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
      const isLt10M = file.size / 1024 / 1024 < 10

      if (!isValidType) {
        this.$message.error('只能上传txt/pdf/doc/docx格式文件!')
        return false
      }
      if (!isLt10M) {
        this.$message.error('文件大小不能超过 10MB!')
        return false
      }

      // 显示进度对话框
      this.progressVisible = true
      this.uploadProgress = 0
      this.progressStatus = {
        icon: 'el-icon-upload',
        text: '正在上传文件...',
        type: ''
      }

      // 模拟上传进度
      this.simulateProgress()
      return true
    },

    // 模拟上传进度
    simulateProgress() {
      let progress = 0
      const timer = setInterval(() => {
        if (progress < 90) {
          progress += Math.random() * 10
          this.uploadProgress = Math.floor(progress)
        } else {
          clearInterval(timer)
        }
      }, 500)
    },

    // 上传成功
    uploadSuccess(response) {
      if (response.code === 200) {
        // 更新进度状态
        this.uploadProgress = 100
        this.progressStatus = {
          icon: 'el-icon-success',
          text: '文件上传成功!',
          type: 'success'
        }
        
        // 延迟关闭进度框
        setTimeout(() => {
          this.progressVisible = false
          this.$message.success('文件已成功上传到知识库')
        }, 1000)
      } else {
        this.handleUploadError(response.message)
      }
    },

    // 上传失败
    uploadError(err) {
      this.handleUploadError(err.message || '上传失败')
    },

    // 处理上传错误
    handleUploadError(message) {
      this.progressStatus = {
        icon: 'el-icon-error',
        text: '上传失败: ' + message,
        type: 'exception'
      }
      
      setTimeout(() => {
        this.progressVisible = false
        this.$message.error(message || '上传失败')
      }, 1000)
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
</style> 