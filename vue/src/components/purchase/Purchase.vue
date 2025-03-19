<template>
  <div class="purchase">
    <el-card class="purchase-hd" shadow="never">
      <div slot="header" style="text-align: left;">
        <i class="el-icon-search"/>
        <span>筛选搜索</span>
        <el-button class="el-button-search" style="right: 111px" type="primary" @click="queryPurchase()">查询
        </el-button>
        <el-button class="el-button-search" style="right: 20px" @click="resetForm('queryForm')">重置</el-button>
      </div>
      <el-form ref="queryForm" :model="queryForm" class="query-form" inline>
        <el-form-item label="采购编号：" prop="purchaseNo">
          <el-input v-model="queryForm.purchaseNo" placeholder="采购编号"
                    @keyup.enter.native="queryPurchase"></el-input>
        </el-form-item>
        <el-form-item label="农产品编号：" prop="productNo">
          <el-input v-model="queryForm.productNo" placeholder="农产品编号"
                    @keyup.enter.native="queryPurchase"></el-input>
        </el-form-item>
        <el-form-item label="操作员：" prop="user">
          <el-input v-model="queryForm.user" placeholder="帐号/姓名" @keyup.enter.native="queryPurchase"></el-input>
        </el-form-item>
        <el-form-item label="采购状态：" prop="status">
          <el-select v-model="queryForm.status" clearable filterable placeholder="全部"
                     @keyup.enter.native="queryPurchase">
            <el-option v-for="item in purchaseStates" :key="item" :label="item" :value="item"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="purchase-box" shadow="never">
      <div slot="header" style="text-align: left;position: relative">
        <i class="el-icon-coin"/>
        <span>数据列表</span>
      </div>
      <el-table :data="purchaseData" border class="type-table" height="425" style="width: 100%;user-select: text">
        <el-table-column label="编号" prop="purchaseId" width="80"></el-table-column>
        <el-table-column label="采购信息编号" prop="purchaseNo" width="150"></el-table-column>
        <el-table-column label="采购时间" prop="purchaseTime" width="180"></el-table-column>
        <el-table-column label="农产品编号" width="140">
          <template slot-scope="scope">
            <el-tooltip v-if="purchaseData" :content="'农产品名称：'+purchaseData[scope.$index].productName" class="item" effect="dark"
                        placement="right">
              <p style="cursor: pointer" v-text="purchaseData[scope.$index].productNo"></p>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="采购数量" prop="purchaseNumber" width="90"></el-table-column>
        <el-table-column label="供应商编号" width="180">
          <template slot-scope="scope">
            <el-tooltip v-if="purchaseData" :content="'供应商：'+purchaseData[scope.$index].supplierName" class="item" effect="dark"
                        placement="right">
              <p style="cursor: pointer" v-text="purchaseData[scope.$index].supplierNo"></p>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作员帐号" width="180">
          <template slot-scope="scope">
            <el-tooltip v-if="purchaseData" :content="'操作员：'+purchaseData[scope.$index].userName" class="item" effect="dark"
                        placement="right">
              <p style="cursor: pointer" v-text="purchaseData[scope.$index].accountNumber"></p>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="收货时间" prop="receiptTime" width="180"></el-table-column>
        <el-table-column label="采购状态" width="100">
          <template slot-scope="scope">
            <span v-if="purchaseData[scope.$index].receiptStatus">已收货</span>
            <span v-else>未收货</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" min-width="150">
          <template slot-scope="scope">
            <el-button v-if="purchaseData[scope.$index].receiptStatus" size="mini" @click="lookOver(scope.$index)">
              查看详情
            </el-button>
            <el-button v-else slot="reference" size="mini" type="danger" @click="receipts(scope.$index)">确认收货
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog :close-on-click-modal="false" :visible.sync="dialogFormVisible" center class="chang-purchase"
               title="采购信息" width="800px">
      <el-form ref='changeFrom' :model="alterForm" class="info-form" inline>
        <el-form-item label="采购编号：" prop="purchaseNo">
          <el-input v-model="alterForm.purchaseNo" placeholder="采购编号" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="采购数量：" prop="purchaseNumber">
          <el-input v-model="alterForm.purchaseNumber" placeholder="采购数量" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="农产品编号：" prop="productNo">
          <el-input v-model="alterForm.productNo" placeholder="农产品编号" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="农产品名称：" prop="productName">
          <el-input v-model="alterForm.productName" placeholder="农产品名称" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="供应商编号：" prop="supplierNo">
          <el-input v-model="alterForm.supplierNo" placeholder="供应商编号" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="供应商名称：" prop="supplierName">
          <el-input v-model="alterForm.supplierName" placeholder="供应商名称" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="操作员帐号：" prop="accountNumber">
          <el-input v-model="alterForm.accountNumber" placeholder="操作员帐号" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="操作员名称：" prop="userName">
          <el-input v-model="alterForm.userName" placeholder="操作员名称" readonly size="small"></el-input>
        </el-form-item>
        <el-form-item label="收货时间：" prop="receiptTime">
          <el-input v-model="alterForm.receiptTime" placeholder="收货时间" readonly size="small"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button style="padding: 12px 80px;" type="primary" @click="dialogFormVisible=false">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Purchase",
  data() {
    return {
      purchaseData: null,
      purchaseResource: null,
      handleIndex: 0,
      dialogFormVisible: false,
      purchaseStates: ['未收货', '已收货'],
      queryForm: {
        purchaseNo: '',
        productNo: '',
        user: '',
        status: ''
      },
      alterForm: {
        purchaseNo: '',
        productNo: '',
        productName: '',
        purchaseNumber: '',
        receiptTime: '',
        supplierNo: '',
        supplierName: '',
        accountNumber: '',
        userName: ''
      }
    }
  },
  methods: {
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    receipts(index) {
      let purchaseNumber = this.purchaseData[index].purchaseNumber; //采购数量
      let productNo = this.purchaseData[index].productNo;  //农产品编号
      let purchaseId = this.purchaseData[index].purchaseId;
      this.$confirm('请核对农产品名称及农产品数量是否正确', '提示', {
        confirmButtonText: '收货',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let loading = this.$loading({lock: true, text: "数据提交中", background: "rgba(255,255,255,0.1)"});
        this.$http.post('/purchase/receipts?productNo=' + productNo + "&purchaseId=" + purchaseId + "&purchaseNumber=" + purchaseNumber).then((rep) => {
          loading.close();
          if (rep.data.code === 200) {
            this.reqInfo();
            this.$notify({
              title: '成功',
              message: this.purchaseData[index].productName + '\t' + rep.data.message,
              type: 'success'
            });
          } else {
            this.$notify.error({title: '失败', message: rep.data.message});
          }
        }).catch((err) => {
          loading.close();
          this.$msg.error(err)
        })
      }).catch(() => {
      });
    },
    lookOver(index) {
      this.alterForm = this.purchaseData[index];
      this.dialogFormVisible = true;
    },
    queryPurchase() {
      let queryData = this.queryForm;
      if (queryData.purchaseNo.length === 0 &&
        queryData.productNo.length === 0 &&
        queryData.user.length === 0 &&
        queryData.status.length === 0) {
        this.purchaseData = this.purchaseResource;
      } else {
        this.purchaseData = [];
        for (let i = 0, len = this.purchaseResource.length; i < len; i++) {
          if (queryData.purchaseNo.length !== 0) {
            if (this.purchaseResource[i].purchaseNo.indexOf(queryData.purchaseNo) !== -1) {
              this.purchaseData.push(this.purchaseResource[i]);
            }
          }
          if (queryData.productNo.length !== 0) {
            if (this.purchaseResource[i].productNo.indexOf(queryData.productNo) !== -1) {
              this.purchaseData.push(this.purchaseResource[i]);
            }
          }
          if (queryData.user.length !== 0) {
            if (this.purchaseResource[i].userName.indexOf(queryData.user) !== -1) {
              this.purchaseData.push(this.purchaseResource[i]);
            } else if (this.purchaseResource[i].accountNumber.indexOf(queryData.user) !== -1) {
              this.purchaseData.push(this.purchaseResource[i]);
            }
          }
          if (queryData.status.length !== 0) {
            if (queryData.status.indexOf('已收货') !== -1 && this.purchaseResource[i].receiptStatus) {
              this.purchaseData.push(this.purchaseResource[i]);
            } else if (queryData.status.indexOf('未收货') !== -1 && (!this.purchaseResource[i].receiptStatus)) {
              this.purchaseData.push(this.purchaseResource[i]);
            }
          }
        }
      }
    },
    isSupplierState(index) {
      this.$http.post('/purchase/updateSupplier?purchaseId=' + this.purchaseData[index].purchaseId + '&receiptStatus=' + this.purchaseData[index].receiptStatus).then((rep) => {
        if (rep.data.code === 200) {
          if (this.purchaseData[index].receiptStatus) {
            this.$notify({
              title: '成功',
              message: '供应商（' + this.purchaseData[index].purchaseName + '）\t已启用',
              type: 'success'
            });
          } else {
            this.$notify({
              title: '成功',
              message: '供应商（' + this.purchaseData[index].purchaseName + '）\t已禁用',
              type: 'success'
            });
          }
        }
      }).catch((err) => {
        this.$msg.error(err)
      })
    },
    reqInfo() {
      this.$http.post('/purchase/findPurchaseAll').then((rep) => {
        if (rep.data.code === 200) {
          this.purchaseResource = this.purchaseData = rep.data.data;
        }
      }).catch((err) => {
        this.$msg.error(err)
      })
    }
  },
  created() {
    this.reqInfo();
  }
}
</script>

<style>
.purchase {
  margin: 0;
  padding: 0;
  min-height: 94%;
  user-select: none;
  min-width: 1226px;
}

.purchase .purchase-hd {
  height: 138px;
  margin: 30px auto 10px;
  width: 95%;
}

.purchase .purchase-hd .el-card__header {
  position: relative;
}


.purchase .info-box {
  height: 600px;
  border: 1px solid #EBEEF5;
}

.purchase .purchase-hd .el-button-search {
  position: absolute;
  top: 14px;
  padding: 3px 0;
  width: 70px;
  height: 30px;
}

.purchase .query-form {
  text-align: left;
}

.purchase .purchase-box {
  height: 530px;
  margin: 20px auto 10px;
  width: 95%;
}

.purchase .purchase-box th, .purchase .purchase-box td {
  text-align: center;
}

.purchase .el-table th.gutter {
  display: table-cell !important;
}

.purchase .info-form {
  position: relative;
}

.purchase .el-upload-dragger {
  width: 200px;
  height: 100px;
}


.purchase .purchase-box .el-button-add {
  position: absolute;
  right: 32px;
  top: -5px;
  padding: 3px 0;
  width: 80px;
  height: 32px;
}

.purchase .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 102px;
  line-height: 102px;
  text-align: center;
}

.purchase .chang-purchase .el-dialog--center .el-dialog__body {
  padding: 25px 25px 0;
}

.purchase .chang-purchase .el-form--inline .el-form-item__label {
  width: 100px;
}

.purchase .chang-purchase .el-input__inner {
  width: 200px;
}

.purchase .chang-purchase .el-dialog {
  border-radius: 15px;
}

.purchase .chang-purchase .info-form {
  padding-left: 44px;

}

.purchase .chang-purchase .info-form .el-form-item {
  width: 45%;
}

</style>
