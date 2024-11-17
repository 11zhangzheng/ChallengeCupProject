<template>
  <div class="traceability-container">
    <div class="trace-card trace-header">
      <div class="title">农产品唯一身份标识码</div>
      <div class="barcode">
        <span v-for="(part, index) in formattedTraceCode"
              :key="index"
              class="code-part">
          {{ part }}
        </span>
      </div>
    </div>

    <div class="trace-card trace-status">
      <div class="status-icon">
        <i class="el-icon-check"></i>
      </div>
      <div class="status-text">农产品溯源码验证通过</div>
    </div>

    <div class="trace-card trace-content">
      <div class="product-info">
        <div class="product-image">
            <!-- 取图片的变量在这里 -->
          <div v-if="!traceData.productImage" class="image-placeholder">
            <i class="el-icon-picture-outline"></i>
            <span>暂无图片</span>
          </div>
          <img
            v-else
            :src="traceData.productImage"
            :alt="traceData.productName">
        </div>

        <h3>{{ traceData.productName }}</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">包装规格</span>
            <span class="value">{{ traceData.specification }}</span>
          </div>
          <div class="info-item">
            <span class="label">生产日期</span>
            <span class="value">{{ traceData.productDate }}</span>
          </div>
          <div class="info-item address">
            <span class="label">生产地址</span>
            <span class="value">{{ traceData.productionAddress }}</span>
          </div>
          <div class="info-item">
            <span class="label">负责人</span>
            <span class="value">{{ traceData.managerName }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系电话</span>
            <span class="value">{{ traceData.phone }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Traceability',
  data() {
    return {
      // traceData:{},
      traceData: {
        traceCode: '',
        productName: '',
        productImage: '',
        specification: '',
        productionAddress: '',
        productionDate: '',
        manager: '',
        phone: ''
      }
    }
  },
  computed: {
    formattedTraceCode() {
      const code = this.traceData.traceCode;
      return code.match(/.{1,5}/g) || [];
    }
  },
  created() {
    const productId = this.$route.query.id;
    this.$http.get('api/trace/info?id='+productId).then(res => {
      if(res.data.code===200||res.data.code===20000){
        this.traceData=res.data.data;
      }else{
        this.$message('获取溯源信息失败,请联系管理员处理 ！')
      }
})
    console.log('商品ID:', productId);
  }
}
</script>

<style scoped>
.traceability-container {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  background: #f8fafc;
  min-height: 100vh;
}

.trace-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.trace-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.trace-header {
  border-top: 4px solid #409EFF;
}

.title {
  font-size: 15px;
  color: #606266;
  margin-bottom: 16px;
  font-weight: 500;
}

.barcode {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.code-part {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  letter-spacing: 1px;
  animation: fadeIn 0.5s ease-out;
}

.trace-status {
  text-align: center;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.status-icon {
  margin-bottom: 12px;
}

.status-icon i {
  font-size: 32px;
  color: #67c23a;
  animation: scaleIn 0.5s ease-out;
}

.status-text {
  color: #67c23a;
  font-size: 16px;
  font-weight: 500;
}

.trace-content {
  background: white;
}

.product-info h3 {
  margin-bottom: 24px;
  font-size: 20px;
  color: #303133;
  text-align: center;
  font-weight: 600;
}

.info-grid {
  display: grid;
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.info-item:hover {
  background: #f0f7ff;
}

.info-item.address {
  grid-column: 1 / -1;
}

.label {
  color: #909399;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 15px;
  font-weight: 500;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scaleIn {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

@media (min-width: 768px) {
  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.product-image {
  width: 100%;
  max-width: 300px;
  aspect-ratio: 1/1;
  margin: 0 auto 20px;
  border-radius: 12px;
  overflow: hidden;
  background-color: #f5f7fa;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
}

.image-placeholder i {
  font-size: 48px;
  margin-bottom: 8px;
}

.image-placeholder span {
  font-size: 14px;
}
</style>
