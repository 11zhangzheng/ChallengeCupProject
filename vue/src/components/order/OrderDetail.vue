<template>
  <div class="order-detail">
    <div class="order-box">
      <div class="box-title">
        <el-alert class="order-state" type="warning" show-icon>当前订单状态：{{ orderInfo.orderState }}</el-alert>
      </div>
      <div class="order-info">
        <div class="info-title">
          <i class="el-icon-coordinate"></i>
          溯源信息
        </div>
        <div class="info-content traceability-content">
          <div class="qr-container">
            <el-image
              v-if="orderInfo.QRCodeBase64"
              :src="orderInfo.QRCodeBase64"

              class="qr-image"
              fit="contain">
            </el-image>
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
      <div class="order-info" v-if="logistics.receiver.length!==0">
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
      <div class="order-info" v-if="logistics.sender.length!==0">
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
      test: "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",
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
      if (state === '0') {

        this.$http.post('/orderDetail/orderInfo?orderNo=' + orderNo).then((res) => {
          if (res.data.code === 200) {
            console.log("orderInfo", res.data)
            this.orderInfo = res.data.data[0];
            if (res.data.data[1] != null) {
              this.logistics = res.data.data[1];
            } else {
              this.logistics.parcelName = '暂无信息';
            }

          }
        }).catch((err) => {
          console.log(err)
        })
      } else if (state === '1') {
        this.$http.post('/orderDetail/returnInfo?orderNo=' + orderNo).then((res) => {
          if (res.data.code === 200) {
            this.orderInfo = res.data.data[0];
            if (res.data.data[1] != null) {
              this.logistics = res.data.data[1];
            } else {
              this.logistics.parcelName = '暂无信息';
            }

          }
        }).catch((err) => {
          console.log(err)
        })
      }
    },
    getQRCode(traceCode) {
      this.$http.get('api/qr/getQRCodeBytes/?productId=' + traceCode)
        .then(res => {
          this.$message("请求qrcode")
          if (res.data.code == 200 || res.data.code == 20000) {
            this.$message('转换blob成功')
            try {
              var tempFile = new Blob([res.data.data], {type: 'image/jpeg'})
            } catch (e) {
              this.$message(e)
            }
            this.orderInfo.QRCodeBase64 = window.URL.createObjectURL(tempFile);
            this.loadImage(tempFile)
            // this.logistics.traceQRcode = window.URL.createObjectURL(tempFile);
          }
        })
    },
    convertBlobToBase64(blob) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(blob);
      });
    },
    async loadImage(blob) {
      if (this.orderInfo.traceQrCode) {
        try {
          const base64String = await this.convertBlobToBase64(blob);
          this.orderInfo.QRCodeBase64 = base64String;
          console.log("base64", this.orderInfo.QRCodeBase64)
        } catch (error) {
          console.error('Failed to convert blob to Base64:', error);
        }
      }
    }
  },
  created() {
    let orderNo = this.$route.query.orderNo;
    let state = this.$route.query.state;
    this.getOrderInfo(state, orderNo).then(
      res =>
        this.getQRCode(this.orderInfo.traceCode)
    );


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
