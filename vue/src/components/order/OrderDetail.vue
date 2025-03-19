<template>
  <div class="order-detail">
    <div class="order-box">
      <div class="box-title">
        <el-alert class="order-state" show-icon type="warning">当前订单状态：{{ orderInfo.orderState }}</el-alert>
      </div>
      <div class="order-info">
        <div class="info-title">
          <i class="el-icon-coordinate"></i>
          溯源信息
        </div>
        <div class="info-content traceability-content">
          <div class="qr-container">
            <template v-if="orderInfo.QRCodeBase64">
              <img
                :src="orderInfo.QRCodeBase64"
                class="qr-image"
                @error="handleImageError"
              />
            </template>
            <div v-else class="qr-placeholder">
              <i class="el-icon-picture-outline"></i>
              <p>溯源二维码生成中...</p>
            </div>
            <div class="qr-desc">产品唯一溯源二维码</div>
          </div>
          <div class="trace-tips">
            <p>扫描二维码可查看该农产品完整的溯源信息</p>
            <p>包括生产方全部数据</p>
          </div>
        </div>
      </div>
      <div class="order-info">
        <div class="info-title">
          <i class="el-icon-edit"></i>
          基本信息
        </div>
        <div class="info-content">
          <div class="info-row">
            <div class="info-item order-no">
              <div class="item-title">订单编号</div>
              <div class="item-content">{{ orderInfo.orderNo }}</div>
            </div>
            <div class="info-item user-account">
              <div class="item-title">用户帐号</div>
              <div class="item-content">{{ orderInfo.userAccount }}</div>
            </div>
            <div class="info-item user-name">
              <div class="item-title">用户名称</div>
              <div class="item-content">{{ orderInfo.userName }}</div>
            </div>
            <div class="info-item pay-type">
              <div class="item-title">支付方式</div>
              <div class="item-content">{{ orderInfo.payType }}</div>
            </div>
            <div class="info-item order-form">
              <div class="item-title">订单来源</div>
              <div class="item-content">{{ orderInfo.orderFrom }}</div>
            </div>
          </div>
          <div class="info-row">
            <div class="info-item product-no">
              <div class="item-title">农产品编号</div>
              <div class="item-content">{{ orderInfo.productNo }}</div>
            </div>
            <div class="info-item pay-amount">
              <div class="item-title">购买数量</div>
              <div class="item-content">{{ orderInfo.payAmount }}</div>
            </div>
            <div class="info-item pay-price">
              <div class="item-title">支付金额</div>
              <div class="item-content">{{ orderInfo.payPrice }}</div>
            </div>
            <div class="info-item order-time">
              <div class="item-title">下单时间</div>
              <div class="item-content">{{ orderInfo.orderTime }}</div>
            </div>
            <div class="info-item logistics-company">
              <div class="item-title">物流公司</div>
              <div class="item-content">{{ logistics.parcelName }}</div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="logistics.receiver.length!==0" class="order-info">
        <div class="info-title">
          <i class="el-icon-user"></i>
          收货人信息
        </div>
        <div class="info-content" style="height: 120px">
          <div class="info-row">
            <div class="info-item delivery-item receiver">
              <div class="item-title">收货人</div>
              <div class="item-content">{{ logistics.receiver }}</div>
            </div>
            <div class="info-item delivery-item receiver-tel">
              <div class="item-title">手机号码</div>
              <div class="item-content">{{ logistics.receiverTel }}</div>
            </div>
            <div class="info-item delivery-item receiver-add" style="width: 40%!important;">
              <div class="item-title">收货地址</div>
              <div class="item-content">{{ logistics.receiverAdd }}</div>
            </div>
          </div>
        </div>
      </div>
      <div v-if="logistics.sender.length!==0" class="order-info">
        <div class="info-title">
          <i class="el-icon-user-solid"></i>
          发货人信息
        </div>
        <div class="info-content" style="height: 120px">
          <div class="info-row">
            <div class="info-item delivery-item sender">
              <div class="item-title">发货人</div>
              <div class="item-content">{{ logistics.sender }}</div>
            </div>
            <div class="info-item delivery-item sender-tel">
              <div class="item-title">手机号码</div>
              <div class="item-content">{{ logistics.senderTel }}</div>
            </div>
            <div class="info-item delivery-item sender-add" style="width: 40%!important;">
              <div class="item-title">发货地址</div>
              <div class="item-content">{{ logistics.senderAdd }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "OrderDetail",
  data() {
    return {
      orderInfo: {
        orderId: '',
        orderNo: '',
        orderTime: '',
        productNo: '',
        userAccount: '',
        userName: '',
        contactWay: '',
        payPrice: '',
        payAmount: '',
        payType: '',
        orderFrom: '',
        orderState: '',
        acceptAddress: '',
        traceQrCode: '',
        QRCodeBase64: '',
        returnState: false,
        traceCode: ''
      },
      logistics: {
        logisticId: '',
        orderNo: '',
        sender: '',
        senderTel: '',
        senderAdd: '',
        receiver: '',
        receiverTel: '',
        receiverAdd: '',
        parcelName: ''
      }
    }
  },
  methods: {
    async getOrderInfo(state, orderNo) {
      try {
        let res;
        if (state === '0') {
          res = await this.$http.post('/orderDetail/orderInfo?orderNo=' + orderNo);
        } else if (state === '1') {
          res = await this.$http.post('/orderDetail/returnInfo?orderNo=' + orderNo);
        }

        if (res.data.code === 200) {
          console.log("orderInfo", res.data);
          this.orderInfo = res.data.data[0];
          if (res.data.data[1] != null) {
            this.logistics = res.data.data[1];
          } else {
            this.logistics.parcelName = '暂无信息';
          }

          // 在获取到订单信息后，立即获取二维码
          if (this.orderInfo.traceCode) {
            await this.getQRCode(this.orderInfo.traceCode);
          }
        }
      } catch (err) {
        console.error(err);
        this.$message.error('获取订单信息失败');
      }
    },

    async getQRCode(traceCode) {
      try {
        console.log("开始获取二维码, traceCode:", traceCode);
        const url = 'api/qr/getQRCodeBytes/?productId=' + traceCode;

        const res = await this.$http.get(url);
        console.log("二维码响应:", res.data);

        // 检查返回的数据格式
        if (res.data && res.data.flag && res.data.code === 20000 && res.data.data) {
          // 直接设置base64数据
          this.$set(this.orderInfo, 'QRCodeBase64', 'data:image/jpeg;base64,' + res.data.data);

          // 强制更新视图
          this.$forceUpdate();
        } else {
          throw new Error(res.data && res.data.message ? res.data.message : '获取二维码失败');
        }
      } catch (err) {
        console.error('获取二维码失败:', err);
        this.$message.error('获取二维码失败: ' + err.message);
      }
    },

    handleImageError(e) {
      console.error('图片加载失败:', e);
      this.$message.error('二维码图片加载失败');
    }
  },
  created() {
    const orderNo = this.$route.query.orderNo;
    const state = this.$route.query.state;
    // 直接调用 getOrderInfo，不需要 then
    this.getOrderInfo(state, orderNo);
  }
}
</script>

<style>
.order-detail {
  margin: 0;
  padding: 20px 0 0;
  min-height: 100%;
  user-select: none;
  min-width: 1226px;
  background-color: #f6f8f9;
}

.order-detail .order-box {
  width: 95%;
  margin: 0 auto;
  min-height: 600px;
  padding-bottom: 20px;
  border: 1px solid #ebeef5;
  background-color: #fff;
}

.order-detail .order-box .box-title {
  background-color: #f2f6fc;
  height: 52px;
}

.order-detail .order-box .box-title .order-state {
  background-color: #f2f6fc;
  color: #f56c6c;
}

.order-detail .order-box .box-title .order-state {
  padding: 16px 20px;
}

.order-detail .order-box .box-title .order-state .el-alert__description {
  color: #f56c6c;
  font-size: 15px;
  font-weight: 500;
}

.order-detail .order-box .box-title .order-state .el-alert__content {
  padding: 0;
}

.order-detail .order-box .box-title .order-state .el-alert__icon {
  font-size: 19px;
}

.order-detail .order-box .box-title .order-state .el-alert__description {
  margin: 0;
}

.order-detail .order-box .order-info {
  padding: 0 30px;
}

.order-detail .order-box .order-info .info-title {
  margin-top: 20px;
  padding-left: 10px;
  text-align: left;
  margin-bottom: 20px;
}

.order-detail .order-box .order-info .info-content {
  height: 204px;
}

.order-detail .order-box .order-info .info-content .info-row {
  height: 100px;
  position: relative;
}

.order-detail .order-box .order-info .info-content .info-row .info-item {
  width: 20%;
  height: 100px;
  position: absolute;
  border: 1px solid #ebeef5;
}

.order-detail .order-box .order-info .info-content .info-row .info-item .item-title {
  height: 35px;
  line-height: 35px;
  border: 1px solid #dcdfe6;
  background-color: #f2f6fc;
}

.order-detail .order-box .order-info .info-content .info-row .info-item .item-content {
  height: 65px;
  line-height: 65px;
}

.order-detail .order-box .order-info .info-content .info-row .order-no {
  left: 0;
}

.order-detail .order-box .order-info .info-content .info-row .user-account {
  left: 20%;
}

.order-detail .order-box .order-info .info-content .info-row .user-name {
  left: 40%;
}

.order-detail .order-box .order-info .info-content .info-row .pay-type {
  left: 60%;
}

.order-detail .order-box .order-info .info-content .info-row .order-form {
  left: 80%;
}

.order-detail .order-box .order-info .info-content .info-row .product-no {
  left: 0;
}

.order-detail .order-box .order-info .info-content .info-row .pay-amount {
  left: 20%;
}

.order-detail .order-box .order-info .info-content .info-row .pay-price {
  left: 40%;
}

.order-detail .order-box .order-info .info-content .info-row .order-time {
  left: 60%;
}

.order-detail .order-box .order-info .info-content .info-row .logistics-company {
  left: 80%;
}

.delivery-item {
  width: 30% !important;
}

.order-detail .order-box .order-info .info-content .info-row .sender {
  left: 0;
}

.order-detail .order-box .order-info .info-content .info-row .sender-tel {
  left: 30%;
}

.order-detail .order-box .order-info .info-content .info-row .sender-add {
  left: 60%;
}

.order-detail .order-box .order-info .info-content .info-row .receiver {
  left: 0;
}

.order-detail .order-box .order-info .info-content .info-row .receiver-tel {
  left: 30%;
}

.order-detail .order-box .order-info .info-content .info-row .receiver-add {
  left: 60%;
}

.traceability-content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px !important;
  background: #f8f9fa;
}

.qr-container {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.qr-image {
  width: 200px;
  height: 200px;
}

.qr-placeholder {
  width: 200px;
  height: 200px;
  background: #f5f7fa;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
}

.qr-placeholder i {
  font-size: 48px;
  color: #909399;
  margin-bottom: 10px;
}

.qr-placeholder p {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.qr-desc {
  margin-top: 15px;
  color: #606266;
  font-size: 14px;
}

.trace-tips {
  margin-left: 40px;
  color: #909399;
}

.trace-tips p {
  margin: 5px 0;
  font-size: 14px;
}

</style>
