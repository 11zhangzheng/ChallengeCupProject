<template>
  <div class="slide-show">
    <el-card class="slideshow-hd" shadow="never">
      <div slot="header" style="text-align: left;">
        <i class="el-icon-search"/>
        <span>筛选搜索</span>
        <el-button class="el-button-search" style="right: 111px" type="primary" @click="queryProductName()">查询
        </el-button>
        <el-button class="el-button-search" style="right: 20px" @click="resetForm('productNameForm')">重置</el-button>
      </div>
      <el-form ref="productNameForm" :model="productNameForm" class="query-form" inline>
        <el-form-item label="输入搜索：" prop="key">
          <el-input v-model="productNameForm.key" clearable placeholder="农产品名称"
                    @keyup.enter.native="queryProductName"></el-input>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="slideshow-box" shadow="never">
      <div slot="header" style="text-align: left;position: relative">
        <i class="el-icon-coin"/>
        <span>数据列表</span>
        <el-button class="el-button-add" @click="addBanner">添加</el-button>
      </div>
      <el-table :data="slideShow" border class="type-table" height="425" style="width: 100%">
        <el-table-column label="编号" prop="bannerId" width="100"></el-table-column>
        <el-table-column label="农产品名称" prop="productName" width="300"></el-table-column>
        <el-table-column align="center" label="轮播图" width="300">
          <template slot-scope="scope">
            <el-image v-if="scope.row.bannerUrl"
                      :preview-src-list="[scope.row.bannerUrl]"
                      :src="scope.row.bannerUrl"
                      fit="fit"
                      style="width: 275px; height: 100px"/>
          </template>
        </el-table-column>
        <el-table-column align="center" label="农产品链接" width="400">
          <template slot-scope="scope">
            <el-tooltip v-if="scope.row.productUrl" :content="scope.row.productUrl" class="item" effect="dark"
                        placement="bottom">
              <el-link :href="scope.row.productUrl" type="warning"><i class="el-icon-view el-icon--right"/> 查看农产品
              </el-link>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column align="center" label="操作" min-width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="openChange(scope.$index)">编辑</el-button>
            <el-button slot="reference" size="mini" type="danger" @click="deleteBanner(scope.$index, slideShow)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog :close-on-click-modal="false" :visible.sync="dialogFormVisible" center
               class="chang-product" title="编辑农产品轮播图信息">
      <el-form ref='changeFrom' :model="alterForm" :rules="alterRules" class="info-form" inline>
        <el-form-item label="农产品名称：" prop="productName" style="width: 100%">
          <el-input v-model="alterForm.productName" placeholder="农产品名称" size="small"></el-input>
        </el-form-item>
        <el-form-item label="农产品链接：" prop="productUrl" style="width: 100%">
          <el-input v-model="alterForm.productUrl" autosize placeholder="农产品购买页链接" size="small"></el-input>
        </el-form-item>
        <el-form-item label="农产品宣传图" prop="bannerUrl" style="">
          <el-upload
            ref="bannerUpload"
            :action="this.$VAR.http+'/uploadImage?name=bannerImage'"
            :before-upload="beforeAvatarUpload"
            :drag="true"
            :on-success="uploadSuccess"
            class="product-upload"
            list-type="picture"
            style="display: flex;justify-content: center;align-items: center;height: 100px;width: 217px">
            <el-image v-if="alterForm.bannerUrl" :src="alterForm.bannerUrl"
                      style="height: 100px;width: 217px"></el-image>
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button style="padding: 12px 43px;margin: 0 30px;" type="primary" @click="submitChange('changeFrom')">确 定
        </el-button>
        <el-button style="padding: 12px 43px;margin: 0 30px;" @click="cancelChange('changeFrom')">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "SlideShow",
  data() {
    return {
      slideShow: null,
      slideShowResource: null,
      handleIndex: 0,
      dialogFormVisible: false,
      insert: false,   //插入时的标志
      productNameForm: {
        key: '',
      },
      alterForm: {
        bannerId: '',
        productName: '',
        productUrl: '',
        bannerUrl: ''
      },
      alterRules: {
        productName: [{required: true, message: '请输入农产品名称', trigger: 'blur'},
          {min: 1, max: 30, message: '名称长度不允许超过 30 个字符', trigger: 'blur'}],
        productUrl: [{required: true, message: '请输入农产品购买页面链接', trigger: 'blur'},
          {min: 1, max: 100, message: '农产品链接长度不允许超过 200 个字符', trigger: 'blur'}],
        bannerUrl: [{required: true, message: '请上传宣传图', trigger: 'blur'}],
      }
    }
  },
  methods: {
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    queryProductName() {
      if (this.productNameForm.key.length !== 0) {
        this.slideShow = [];
        for (let i = 0, len = this.slideShowResource.length; i < len; i++) {
          if (this.slideShowResource[i].productName.indexOf(this.productNameForm.key) !== -1) {
            this.slideShow.push(this.slideShowResource[i]);
          }
        }
      } else {
        this.slideShow = this.slideShowResource;
      }
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
    openChange(index) {
      this.handleIndex = index;
      this.insert = false;
      this.alterForm = this.slideShow[index];
      this.dialogFormVisible = true;
    },
    //删除类别操作
    deleteBanner(index, rows) {
      let name = this.slideShow[index].productName;
      this.$confirm('此操作将永久删除该宣传图信息, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let loading = this.$loading({lock: true, text: "数据提交中", background: "rgba(255,255,255,0.1)"});
        this.$http.post('/banner/deleteById?bannerId=' + this.slideShow[index].bannerId).then((rep) => {
          loading.close();
          if (rep.data.code === 200) {
            rows.splice(index, 1);//删掉该行
            this.$notify({title: '成功', message: '宣传图(' + name + ')\t移除成功', type: 'success'});
          } else {
            this.$notify({title: '错误', message: rep.data.message, type: 'error'});
          }
        }).catch((err) => {
          loading.close();
          this.$msg.error(err);
        })
      }).catch(() => {
        this.$notify({title: '成功', message: '操作取消', type: 'success'});
      });
    },
    submitChange(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.insert) {
            this.alterForm.bannerId = null;
            let loading = this.$loading({lock: true, text: "数据提交中", background: "rgba(255,255,255,0.1)"});
            this.$http.post('/banner/add', this.$qs.stringify(this.alterForm, {skipNulls: true})).then((rep) => {
              loading.close();
              if (rep.data.code === 200) {
                this.dialogFormVisible = false;
                this.reqInfo();
                this.$notify({
                  title: '成功',
                  message: this.alterForm.productName + '\t宣传图添加成功',
                  type: 'success'
                });
              } else {
                this.$notify({title: '错误', message: rep.data.message, type: 'error'});
              }
            }).catch((err) => {
              loading.close();
              this.$msg.error(err)
            })
          } else {
            let loading = this.$loading({lock: true, text: "数据提交中", background: "rgba(255,255,255,0.1)"});
            this.$http.post('/banner/update', this.$qs.stringify(this.alterForm)).then((rep) => {
              loading.close();
              if (rep.data.code === 200) {
                this.dialogFormVisible = false;
                this.slideShow[this.handleIndex] = this.alterForm;
                this.$notify({title: '成功', message: '宣传图信息更新成功', type: 'success'});
              } else {
                this.$notify({title: '错误', message: rep.data.message, type: 'error'});
              }
            }).catch((err) => {
              loading.close();
              this.$msg.error(err)
            })
          }
        } else {
          return false;
        }
      });
    },
    cancelChange(formName) {
      this.dialogFormVisible = false;
      this.$refs[formName].resetFields();
      this.$notify({title: '操作取消', message: '', type: 'success'});
    },
    uploadSuccess(response) {
      if (response.code === 200) {
        this.$msg.success("宣传图上传成功！")
        this.alterForm.bannerUrl = response.data;
        this.$refs['bannerUpload'].clearFiles();
      } else {
        this.$msg.error("宣传图上传失败！")
      }
    },
    addBanner() {
      this.alterForm.productName = '';
      this.alterForm.productUrl = '';
      this.alterForm.bannerUrl = '';
      this.insert = true;
      this.dialogFormVisible = true;
    },
    reqInfo() {
      let loading = this.$loading({lock: true, text: "数据加载中", background: "rgba(255,255,255,0.1)"});
      this.$http.post('/banner/findAll').then((rep) => {
        loading.close();
        if (rep.data.code === 200) {
          this.slideShowResource = this.slideShow = rep.data.data;
        }
      }).catch((err) => {
        loading.close();
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
.slide-show {
  margin: 0;
  padding: 0;
  min-height: 94%;
  user-select: none;
  min-width: 1226px;
}

.slide-show .slideshow-hd {
  height: 138px;
  margin: 30px auto 10px;
  width: 95%;
}

.slide-show .slideshow-hd .el-card__header {
  position: relative;
}


.slide-show .info-box {
  height: 600px;
  border: 1px solid #EBEEF5;
}

.slide-show .slideshow-hd .el-button-search {
  position: absolute;
  top: 14px;
  padding: 3px 0;
  width: 70px;
  height: 30px;
}

.slide-show .query-form {
  text-align: left;
}

.slide-show .el-dialog {
  width: 600px;
}

.chang-product .el-dialog__body {
  padding: 25px 60px 15px;
}


.slide-show .slideshow-box {
  height: 530px;
  margin: 20px auto 10px;
  width: 95%;
}

.slide-show .slideshow-box th, .slide-show .slideshow-box td {
  text-align: center;
}

.slide-show .el-table th.gutter {
  display: table-cell !important;
}

.slide-show .info-form {
  position: relative;
}

.slide-show .info-form .el-form-item__content {
  width: 60%;
}

.slide-show .el-upload-dragger {
  width: 200px;
  height: 100px;
}

.slide-show .el-textarea__inner {
  height: 280px !important;
  width: 188px !important;
}

.slide-show .slideshow-box .el-button-add {
  position: absolute;
  right: 32px;
  top: -5px;
  padding: 3px 0;
  width: 80px;
  height: 32px;
}

.slide-show .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 102px;
  line-height: 102px;
  text-align: center;
}
</style>
