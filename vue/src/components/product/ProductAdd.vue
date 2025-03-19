<!--添加农产品组件-->
<template>
  <div class="product-add">
    <el-card class="product-box" shadow="hover">
      <div slot="header" style="text-align: left">
        <i class="el-icon-circle-plus-outline"/>
        <span>添加农产品</span>
      </div>
      <div class="info-box">
        <el-form ref="ruleForm" :inline="true" :model="productForm" :rules="productRules" class="product-form">
          <el-form-item label="农产品名称：" prop="productName" style="width: 100%">
            <el-input v-model="productForm.productName" size="small" style="width: 300px"></el-input>
          </el-form-item>
          <el-form-item label="农产品分类：" prop="productType" style="width: 45%">
            <el-select v-model="productForm.productType" filterable placeholder="请选择类别"
                       size="small" @change="querySpec(productForm.productType)">
              <el-option v-for="(item,index) in productTypeInfo" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="农产品规格：" prop="productSpec" style="width: 50%">
            <el-select v-model="productSpec" class="select-spec" multiple placeholder="请选择农产品规格" size="small"
                       style="width: 245px;">
              <el-option v-for="item in productSpecs" :key="item.specsId" :label="item.specsName"
                         :value="item.specsId"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="农产品品牌：" prop="productBrand" style="width: 100%">
            <el-select v-model="productForm.productBrand" filterable placeholder="请选择品牌" size="small">
              <el-option v-for="(item,index) in productBrandInfo" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="农产品进价：" prop="inPrice" style="width: 45%;">
            <el-input v-model="productForm.inPrice" min=0 placeholder="农产品进价" size="small"
                      type='number'></el-input>
          </el-form-item>
          <el-form-item label="农产品库存：" prop="productStock" style="width: 45%;">
            <el-input-number v-model="productForm.productStock" :max=1000 :min=0 placeholder="农产品库存"
                             size="small"></el-input-number>
          </el-form-item>
          <el-form-item label="农产品售价：" prop="outPrice" style="width: 45%;">
            <el-input v-model="productForm.outPrice" min=0 placeholder="农产品售价" size="small"
                      type='number'></el-input>
          </el-form-item>
          <el-form-item label="最低库存：" prop="lowestStock" style="width: 45%;">
            <el-input-number v-model="productForm.lowestStock" :max=1000 :min=0 placeholder="最低库存"
                             size="small"></el-input-number>
          </el-form-item>
          <el-form-item label="农产品状态：" style="width: 100%;height: 30px">
            <el-form-item label="上架" prop="isSale" style="margin-right:50px">
              <el-switch v-model="productForm.isSale"></el-switch>
            </el-form-item>
            <el-form-item label="新品" prop="isNew">
              <el-switch v-model="productForm.isNew"></el-switch>
            </el-form-item>
          </el-form-item>
          <el-form-item label="农产品介绍：" prop="productDescribe" style="width: 45%; margin-right: 5%">
            <el-input
              v-model="productForm.productDescribe"
              :autosize="{ minRows: 1, maxRows: 4}"
              class="product-desc"
              placeholder="农产品描述"
              show-word-limit
              size="small"
              type="textarea">
            </el-input>
          </el-form-item>
          <el-form-item label="农产品图片：" prop="productUrl" style="width: 45%;">
            <el-upload
              ref="productUpload"
              :action="this.$VAR.http+'/uploadImage?name=productImage'"
              :before-upload="beforeAvatarUpload"
              :drag="true"
              :on-success="uploadProductUrlSuccess"
              class="product-upload"
              list-type="picture"
              style="display: flex;justify-content: center;align-items: center;height: 140px;width: 160px">
              <el-image v-if="productForm.productUrl" :src="productForm.productUrl"
                        style="height: 140px;width: 160px"></el-image>
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item style="width:100%;text-align: center;position: absolute;bottom: -14px;left: 0">
            <el-button style="margin: 0 55px" type="primary" @click="submitForm('ruleForm')">添加农产品</el-button>
            <el-button style="margin: 0 55px" @click="resetForm('ruleForm')">取消重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "ProductAdd",
  data() {
    return {
      productBrandInfo: '',
      productTypeInfo: '',
      productId: '',
      productForm: {
        productNo: '',
        productName: '',
        productBrand: '',
        productType: '',
        productDescribe: '',
        inPrice: 0,
        outPrice: 0,
        productStock: 50,
        lowestStock: 50,
        isNew: true,
        isSale: true,
        isStockOut: false,
        productUrl: '',
      },
      productSpec: [],
      productSpecs: [],
      productRules: {
        productName: [{required: true, message: '请填写农产品名称', trigger: 'blur'},
          {min: 1, max: 30, message: '名称长度不允许超过 30 个字符', trigger: 'blur'}],
        productBrand: [{required: true, message: '请选择农产品品牌', trigger: 'blur'}],
        productType: [{required: true, message: '请选择农产品分类', trigger: 'blur'}],
        productDescribe: [{required: true, message: '请填写农产品介绍', trigger: 'blur'},
          {min: 1, max: 100, message: '名称长度不允许超过 100 个字符', trigger: 'blur'}],
        productUrl: [{required: true, message: '请上传农产品图片', trigger: 'blur'}],
      }
    }
  },
  methods: {
    //上传图片URL
    uploadProductUrlSuccess(response) {
      if (response.code === 200) {
        this.$msg.success("农产品图片上传成功！")
        this.productForm.productUrl = response.data;
        this.$refs['productUpload'].clearFiles();
      } else {
        this.$msg.error("农产品图片上传失败！")
      }
    },
    //根据时间获取农产品编号
    getDateNow() {
      let time = new Date(); // 时间戳
      let year = String(time.getFullYear()); // 年
      let mouth = String(time.getMonth() + 1); // 月
      let day = String(time.getDate()); // 日
      let hours = String(time.getHours()); // 时
      if (hours.length < 2) {
        hours = '0' + hours
      }
      let minutes = String(time.getMinutes()); // 分
      if (minutes.length < 2) {
        minutes = '0' + minutes
      }
      let seconds = String(time.getSeconds()); // 秒
      if (seconds.length < 2) {
        seconds = '0' + seconds
      }
      this.$http.post('/product/findCount').then((rep) => {
        if (rep.data.code === 200) {
          let count = rep.data.data;
          this.productForm.productNo = year + mouth + day + hours + minutes + seconds + count;
        } else {
          this.$msg.error(rep.data.message);
        }
      }).catch((err) => {
        this.$msg.error(err);
      })
    },
    querySpec(productType) {
      this.productSpec.length = 0;
      this.$http.post('/specs/findAllByType?productType=' + productType).then((rep) => {
        if (rep.data.code === 200) {
          this.productSpecs = rep.data.data;
        }
      }).catch((err) => {
        this.$msg.error(err)
      })
    },
    //提交表单
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.productForm.productName = this.productForm.productName.trim();
          this.productForm.productDescribe = this.productForm.productDescribe.trim();
          let loading = this.$loading({lock: true, text: "数据提交中", background: "rgba(255,255,255,0.1)"});
          this.productForm.isStockOut = this.productForm.productStock < this.productForm.lowestStock;
          this.$http.post('/product/add', this.$qs.stringify(this.productForm)).then((rep) => {
            loading.close();
            if (rep.data.code === 200) {
              this.$notify({title: '成功', message: this.productForm.productName + '\t添加成功', type: 'success'});
              this.$refs[formName].resetFields();
              this.getDateNow();
              if (this.productSpec.length !== 0) {
                this.productId = null;
                this.submitSpec(this.productForm.productNo);
              }
            } else {
              this.$notify.error({title: '失败', message: rep.data.message});
            }
          }).catch((err) => {
            loading.close();
            this.$msg.error(err)
          })
        } else {
          return false;
        }
      });
    },
    //重置表单
    resetForm(formName) {
      this.getDateNow();
      this.$refs[formName].resetFields();
    },
    sleep(time, productNo) {
      let that = this;
      setTimeout(function () {
        that.$http.post('/product/findIdByKey?productNo=' + productNo).then((rep) => {
          if (rep.data.code === 200) {
            that.productId = rep.data.data;
          }
        }).catch((err) => {
          that.$msg.error(err)
        })
        if (that.productId !== null) {
          that.$http.post('/productSpecs/addBatch?productId=' + that.productId + '&specsIds=' + that.productSpec);
          that.productSpec = [];
        } else {
          that.sleep(1000, productNo);
        }
      }, time);

    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isGIF = file.type === 'image/gif';
      const isPNG = file.type === 'image/png';
      const isBMP = file.type === 'image/bmp';
      const isWEBP = file.type === 'image/webp';
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!(isJPG || isGIF || isPNG || isBMP || isWEBP)) {
        this.$msg.error('仅支持上传 JPG、JPEG、PNG、GIF、BMP、WEBP 格式文件');
      } else {
        if (!isLt5M) {
          this.$msg.error('上传图片大小不能超过 5MB!');
        }
      }
      return (isJPG || isGIF || isPNG || isBMP || isWEBP) && isLt5M;
    },
    submitSpec(productNo) {
      this.sleep(1000, productNo);
    }
  },
  created() {
    this.$http.post('/productBrand/findAllName').then((rep) => {
      if (rep.data.code === 200) {
        this.productBrandInfo = rep.data.data;
      }
    }).catch((err) => {
      this.$msg.error(err)
    })
    this.$http.post('/productType/findAllName').then((rep) => {
      if (rep.data.code === 200) {
        this.productTypeInfo = rep.data.data;
      }
    }).catch((err) => {
      this.$msg.error(err)
    })
    this.getDateNow();
  }
}
</script>

<style>
.product-add {
  margin: 0;
  padding: 0;
  min-height: 100%;
  user-select: none;
  min-width: 1226px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.product-add .product-box {
  height: 780px;
  width: 90%;
}

.product-add .info-box {
  height: 680px;
  border: 1px solid #EBEEF5;
}

.product-add .product-form {
  height: 640px;
  width: 700px;
  margin: 25px auto 0;
  text-align: left;
  position: relative;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
}

.product-add .product-form .el-form-item {
  margin-bottom: 15px;
  display: inline-block;
  vertical-align: top;
}

.product-add .product-upload .el-upload-dragger {
  height: 140px;
  width: 160px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.product-add .product-desc .el-textarea__inner {
  max-height: 140px;
  min-height: 140px;
  width: 100%;
}

.product-add .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.product-add .product-form .el-form-item__label {
  width: 94px;
  padding-right: 10px;
}

.product-add .product-form .select-spec .el-select__tags {
  overflow-y: scroll;
  max-height: 75px;
}

.product-add .product-form .select-spec .el-select__tags::-webkit-scrollbar {
  width: 0;
}

.product-add .product-upload {
  vertical-align: top;
}
</style>
