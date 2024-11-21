<template>
  <div class="mall-container">
    <!-- 主要内容区域 -->
    <div class="ai-planting-content">
      <div class="content-wrapper">
        <h1 class="main-title">智+种植</h1>

        <!-- AI种植介绍部分 -->
        <div class="ai-description">
          <h2>什么是智+种植?</h2>
          <p class="description-text">智+种植是HarvestHub推出的具有AI大模型能力的智能种植问答知识库平台。我们致力于：</p>
          <div class="features-grid">
            <div class="feature-card">
              <i class="el-icon-cpu"></i>
              <h3>智能建议</h3>
              <p>提供智能化的种植建议和解决方案</p>
            </div>
            <div class="feature-card">
              <i class="el-icon-chat-dot-round"></i>
              <h3>AI问答</h3>
              <p>利用AI大模型回答种植相关问题</p>
            </div>
            <div class="feature-card">
              <i class="el-icon-reading"></i>
              <h3>知识分享</h3>
              <p>分享专业的农业技术知识</p>
            </div>
            <div class="feature-card">
              <i class="el-icon-connection"></i>
              <h3>专家连接</h3>
              <p>连接种植者与农业专家</p>
            </div>
          </div>
        </div>

        <!-- AI问答入口 -->
        <div class="ai-chat-entrance">
          <h2>AI智能问答</h2>
          <div class="entrance-card">
            <div class="entrance-content">
              <div class="entrance-icon">
                <i class="el-icon-chat-dot-round"></i>
              </div>
              <div class="entrance-text">
                <h3>开始智能对话</h3>
                <p>让AI助手为您解答种植难题</p>
                <el-button
                  type="primary"
                  size="large"
                  @click="$router.push('/chatPage')"
                  class="start-button"
                >
                  开始智能问答
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 商家入驻信息 -->
        <div class="merchant-join">
          <h2>商家入驻</h2>
          <div class="join-card">
            <div class="join-content">
              <h3>欢迎优质种植商家入驻 HarvestHub！</h3>
              <p>如果您想要成为我们的合作伙伴，请联系管理员：</p>
              <div class="contact-info">
                <i class="el-icon-message"></i>
                <span>admin@qq.com</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.mall-container {
  background: #FAFDF6;
  min-height: 100vh;
  overflow-x: hidden;
  padding-top: 20px;
}

.ai-planting-content {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.main-title {
  font-size: 42px;
  font-weight: bold;
  color: #2C5530;
  text-align: center;
  margin: 40px 0;
  position: relative;
}

.main-title::after {
  content: '';
  display: block;
  width: 60px;
  height: 4px;
  background: #4A6741;
  margin: 15px auto;
  border-radius: 2px;
}

.ai-description {
  margin: 60px 0;
}

.ai-description h2 {
  color: #2C5530;
  font-size: 32px;
  margin-bottom: 20px;
  text-align: center;
}

.description-text {
  text-align: center;
  color: #4A6741;
  font-size: 18px;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.feature-card {
  background: white;
  padding: 30px;
  border-radius: 15px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid #E8F0E5;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(44, 85, 48, 0.1);
}

.feature-card i {
  font-size: 36px;
  color: #4A6741;
  margin-bottom: 20px;
}

.feature-card h3 {
  color: #2C5530;
  font-size: 20px;
  margin-bottom: 15px;
}

.feature-card p {
  color: #4A6741;
  line-height: 1.6;
}

.ai-chat-entrance {
  margin: 60px 0;
}

.ai-chat-entrance h2 {
  color: #2C5530;
  font-size: 32px;
  text-align: center;
  margin-bottom: 30px;
}

.entrance-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(44, 85, 48, 0.1);
  border: 1px solid #E8F0E5;
}

.entrance-content {
  display: flex;
  align-items: center;
  gap: 40px;
}

.entrance-icon {
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F0F7ED;
  border-radius: 15px;
  flex-shrink: 0;
}

.entrance-icon i {
  font-size: 80px;
  color: #4A6741;
}

.entrance-text {
  flex: 1;
}

.entrance-text h3 {
  color: #2C5530;
  font-size: 24px;
  margin-bottom: 15px;
}

.entrance-text p {
  color: #4A6741;
  font-size: 16px;
  margin-bottom: 30px;
}

.start-button {
  background: #4A6741;
  border-color: #4A6741;
  padding: 15px 30px;
  font-size: 18px;
  transition: all 0.3s ease;
}

.start-button:hover {
  background: #2C5530;
  border-color: #2C5530;
  transform: translateY(-2px);
}

.merchant-join {
  margin: 60px 0;
}

.merchant-join h2 {
  color: #2C5530;
  font-size: 32px;
  text-align: center;
  margin-bottom: 30px;
}

.join-card {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(44, 85, 48, 0.1);
  border: 1px solid #E8F0E5;
}

.join-content {
  text-align: center;
}

.join-content h3 {
  color: #2C5530;
  font-size: 24px;
  margin-bottom: 20px;
}

.join-content p {
  color: #4A6741;
  font-size: 16px;
  margin-bottom: 20px;
}

.contact-info {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: #F0F7ED;
  padding: 15px 30px;
  border-radius: 10px;
  margin-top: 20px;
}

.contact-info i {
  color: #4A6741;
  font-size: 24px;
}

.contact-info span {
  color: #2C5530;
  font-size: 20px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }

  .entrance-content {
    flex-direction: column;
    text-align: center;
  }

  .entrance-icon {
    margin: 0 auto;
  }
}
</style>
