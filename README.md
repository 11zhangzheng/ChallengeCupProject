# ChangeCupProject
大挑项目前后端
# 项目介绍
1. 前端基于vue+element-admin+axios+vue-router+vuex
2. 后端基于mybatisPlus+mysql+java+springboot
# 项目部署
1. 前端部署
npm install --legacy-peer-deps
npm run dev
2. 后端部署
mvn clean install
mvn spring-boot:run

已经把数据库改成云数据库了，端口什么的可以不用改了；
redis目前还是127.0.0.1，端口6379
运行环境环境：JDK 1.8、Node 14、MySQL 8、Navicat（任何桌面操作工具可平替）、IDEA
-管理员的账号密码都是admin@qq.com
-系统中只要是涉及到输入“验证码”的都是“123456”
-用户跳前台，管理员跳后台，有登录验证
