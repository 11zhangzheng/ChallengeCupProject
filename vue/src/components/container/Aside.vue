<template>
  <div class="aside">
    <el-menu
      ref="menu"
      active-text-color="#409EFF"
      background-color="#304156"
      class="el-menu-vertical-demo"
      default-active="2"
      text-color="#bfcbd9"
      unique-opened
      @close="handleClose"
      @open="handleOpen">
      <router-link class="router-link" to="/HomePage">
        <el-menu-item id="HomePage" index="1">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>
      </router-link>
      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-shopping-bag-1"/>
          <span>农产品</span>
        </template>
        <el-menu-item-group>
          <router-link class="router-link" to="/ProductList">
            <el-menu-item id="ProductList" class="el-menu-item-bg" index="2-1">
              <i class="el-icon-milk-tea"/>
              <span>农产品列表</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/ProductAdd">
            <el-menu-item id="ProductAdd" class="el-menu-item-bg" index="2-2">
              <i class="el-icon-circle-plus-outline"/>
              <span>添加农产品</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/ProductType">
            <el-menu-item id="ProductType" class="el-menu-item-bg" index="2-3">
              <i class="el-icon-menu"/>
              <span>农产品分类</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/ProductBrand">
            <el-menu-item id="ProductBrand" class="el-menu-item-bg" index="2-4">
              <i class="el-icon-discount"/>
              <span>农产品品牌</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/ProductSpecs">
            <el-menu-item id="ProductSpecs" class="el-menu-item-bg" index="2-5">
              <i class="el-icon-cloudy"/>
              <span>农产品规格</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/Purchase">
            <el-menu-item id="Purchase" class="el-menu-item-bg" index="2-6">
              <i class="el-icon-truck"/>
              <span>农产品采购</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/Supplier">
            <el-menu-item id="Supplier" class="el-menu-item-bg" index="2-7">
              <i class="el-icon-s-custom"/>
              <span>供应商管理</span>
            </el-menu-item>
          </router-link>
        </el-menu-item-group>
      </el-submenu>

      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-s-order"/>
          <span>订单</span>
        </template>
        <el-menu-item-group>
          <router-link class="router-link" to="/OrderList">
            <el-menu-item id="OrderList" class="el-menu-item-bg" index="3-1">
              <i class="el-icon-news"/>
              <span>订单列表</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/ReturnGoods">
            <el-menu-item id="ReturnGoods" class="el-menu-item-bg" index="3-2">
              <i class="el-icon-box"/>
              <span>退货申请处理</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/ReturnReason">
            <el-menu-item id="ReturnReason" class="el-menu-item-bg" index="3-3">
              <i class="el-icon-edit-outline"/>
              <span>退货原因设置</span>
            </el-menu-item>
          </router-link>
        </el-menu-item-group>
      </el-submenu>


      <el-submenu index="4">
        <template slot="title">
          <i class="el-icon-sell"/>
          <span>营销</span>
        </template>
        <el-menu-item-group>
          <router-link class="router-link" to="/SlideShow">
            <el-menu-item id="SlideShow" class="el-menu-item-bg" index="4-1">
              <i class="el-icon-picture"/>
              <span>农产品轮播图</span>
            </el-menu-item>
          </router-link>
        </el-menu-item-group>
      </el-submenu>


      <el-submenu index="5">
        <template slot="title">
          <i class="el-icon-bell"/>
          <span>权限</span>
        </template>
        <el-menu-item-group>
          <router-link class="router-link" to="/AdminList">
            <el-menu-item id="AdminList" class="el-menu-item-bg" index="5-1">
              <i class="el-icon-eleme"/>
              <span>管理员列表</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/UserList">
            <el-menu-item id="UserList" class="el-menu-item-bg" index="5-1">
              <i class="el-icon-user"/>
              <span>顾客列表</span>
            </el-menu-item>
          </router-link>
          <router-link class="router-link" to="/RoleList">
            <el-menu-item id="RoleList" class="el-menu-item-bg" index="5-2">
              <i class="el-icon-connection"/>
              <span>角色列表</span>
            </el-menu-item>
          </router-link>
        </el-menu-item-group>
      </el-submenu>

      <!-- 添加智+种植菜单 -->
      <el-submenu index="6">
        <template slot="title">
          <i class="el-icon-umbrella"/>
          <span>智能农业</span>
        </template>
        <el-menu-item-group>
          <router-link class="router-link" to="/aiPlanting">
            <el-menu-item id="AiPlanting" class="el-menu-item-bg" index="6-1">
              <i class="el-icon-magic-stick"/>
              <span>智+种植</span>
            </el-menu-item>
          </router-link>
          <router-link v-if="$store.state.role && $store.state.role.indexOf($VAR.adminRole)!==-1" class="router-link"
                       to="/knowledgeManage">
            <el-menu-item id="KnowledgeManage" class="el-menu-item-bg" index="6-2">
              <i class="el-icon-collection"/>
              <span>知识库上传</span>
            </el-menu-item>
          </router-link>
        </el-menu-item-group>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
import store from "../../store";

export default {
  name: "Aside",
  watch: {
    // 监听路由
    $route(val) {
      let other = document.getElementsByClassName("el-menu-item");
      for (let i = 0; i < other.length; i++) {
        other[i].style.color = 'rgb(191, 203, 217)';
      }
      switch (val.path) {
        case "/HomePage":
          document.getElementById("HomePage").style.color = 'rgb(64, 158, 255)';
          break;
        case "/ProductList":
          document.getElementById("ProductList").style.color = 'rgb(64, 158, 255)';
          break;
        case "/ProductAdd":
          document.getElementById("ProductAdd").style.color = 'rgb(64, 158, 255)';
          break;
        case "/ProductType":
          document.getElementById("ProductType").style.color = 'rgb(64, 158, 255)';
          break;
        case "/ProductBrand":
          document.getElementById("ProductBrand").style.color = 'rgb(64, 158, 255)';
          break;
        case "/ProductSpecs":
          document.getElementById("ProductSpecs").style.color = 'rgb(64, 158, 255)';
          break;
        case "/OrderList":
          document.getElementById("OrderList").style.color = 'rgb(64, 158, 255)';
          break;
        case "/AdminList":
          document.getElementById("AdminList").style.color = 'rgb(64, 158, 255)';
          break;
        case "/UserList":
          document.getElementById("UserList").style.color = 'rgb(64, 158, 255)';
          break;
        case "/RoleList":
          document.getElementById("RoleList").style.color = 'rgb(64, 158, 255)';
          break;
        case "/Purchase":
          document.getElementById("Purchase").style.color = 'rgb(64, 158, 255)';
          break;
        case "/Supplier":
          document.getElementById("Supplier").style.color = 'rgb(64, 158, 255)';
          break;
        case "/ReturnReason":
          document.getElementById("ReturnReason").style.color = 'rgb(64, 158, 255)';
          break;
        case "/ReturnGoods":
          document.getElementById("ReturnGoods").style.color = 'rgb(64, 158, 255)';
          break;
        case "/SlideShow":
          document.getElementById("SlideShow").style.color = 'rgb(64, 158, 255)';
          break;
        case "/aiPlanting":
          document.getElementById("AiPlanting").style.color = 'rgb(64, 158, 255)';
          break;
      }
      if (this.$route.path === '/HomePage') {
        this.$refs.menu.close(window.localStorage.getItem('menu-index'));
      }
    }
  },
  methods: {
    handleOpen(key) {
      window.localStorage.setItem('menu-index', key);
      if (store.state.token === null) {
        this.$notify({
          title: '警告',
          message: '请先登录！',
          type: 'warning'
        });
        this.$router.push('/loginForm');
      }
      let role = this.$store.state.role;
      switch (key) {
        case "2":
          if (role.indexOf(this.$VAR.productRole) === -1 && role.indexOf(this.$VAR.adminRole) === -1) {
            if (this.$route.path !== '/NoAccess') {
              this.$router.push({path: '/NoAccess'});
            }
            this.$refs.menu.close(key);
          }
          break
        case "3":
          if (role.indexOf(this.$VAR.orderRole) === -1 && role.indexOf(this.$VAR.adminRole) === -1) {
            if (this.$route.path !== '/NoAccess') {
              this.$router.push({path: '/NoAccess'});
            }
            this.$refs.menu.close(key);
          }
          break
        case "4":
          if (role.indexOf(this.$VAR.productRole) === -1 && role.indexOf(this.$VAR.adminRole) === -1) {
            if (this.$route.path !== '/NoAccess') {
              this.$router.push({path: '/NoAccess'});
            }
            this.$refs.menu.close(key);
          }
          break
        case "5":
          if (role.indexOf(this.$VAR.userRole) === -1 && role.indexOf(this.$VAR.adminRole) === -1) {
            if (this.$route.path !== '/NoAccess') {
              this.$router.push({path: '/NoAccess'});
            }
            this.$refs.menu.close(key);
          }
          break
        case "6":
          // 智能农业模块不需要特殊权限控制
          break;
      }
    },
    handleClose(key, keyPath) {
      // console.log(key);
      // console.log(keyPath)
    }
  }
}
</script>

<style>
.aside {
  height: 100%;
}

.aside .el-menu-vertical-demo {
  height: 100% !important;
  user-select: none;
  overflow-x: hidden;
}

.aside .el-menu-vertical-demo::-webkit-scrollbar { /*滚动条整体样式*/
  width: 0; /*高宽分别对应横竖滚动条的尺寸*/
}

.aside .el-menu-item-bg {
  background: #1F2D3D !important;
  min-width: auto !important;
  padding-left: 55px;
}

.aside .el-menu-item-bg:hover {
  background: #001528 !important;
}

.aside .el-menu-item-group__title {
  display: none;
}

.aside .el-submenu__title, .el-menu-item {
  text-align: left;
  padding-left: 30px !important;
}

.aside .el-menu-item i {
  color: inherit !important;
}

.aside .router-link {
  text-decoration: none;
  color: #bfcbd9;
}

.aside .router-link:hover {
  text-decoration: none;
  color: #409EFF;
}

.el-menu span {
  display: inline-block;
  margin-left: 5px;
}
</style>
