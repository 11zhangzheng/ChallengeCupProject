# ChallengeCupProject

## 项目概述

ChallengeCupProject 是一个全栈开发项目，包含前端和后端完整实现。

## 技术栈

### 前端

- Vue.js
- Element Admin
- Axios
- Vue Router
- Vuex

### 后端

- Java
- Spring Boot
- MyBatis Plus
- MySQL
- Redis
- Chroma 向量数据库

## 运行环境要求

- JDK 1.8
- Node.js 14
- MySQL 8.0
- Navicat（可使用其他数据库管理工具替代）
- IDEA

## 部署说明

### 前端部署

```bash
# 安装依赖
npm install --legacy-peer-deps

# 启动开发服务器
npm run dev
```

### 后端部署

```bash
# 清理并安装依赖
mvn clean install

# 启动Spring Boot应用
mvn spring-boot:run
```

### Chroma 向量数据库安装与配置

```bash
# 安装 chromadb Python 包
pip install chromadb

# 如果遇到版本兼容问题，可以指定版本安装
pip install chromadb==0.4.15

# 启动 Chroma 服务

# 方式：在项目根目录，命令行启动服务
chroma run --port 8585
```

注意事项：

- 确保已安装 Python 3.7+
- 确保项目根目录下存在 chroma_data 文件夹
- 服务默认端口为 8585
- 首次运行会自动创建必要的数据文件

## 配置说明

### 数据库配置

- 已配置为云数据库，无需修改端口

### Redis配置

- 主机: 127.0.0.1
- 端口: 6379

### Chroma向量数据库配置

- 数据存储位置: 项目根目录下的 chroma_data 文件夹
- 服务端口: 8585
- 访问地址: http://localhost:8585

## 账号信息

### 管理员账号

- 邮箱: admin@qq.com
- 密码: admin@qq.com

### 系统说明

- 所有需要验证码的地方统一使用: 123456
- 用户访问前台界面
- 管理员访问后台界面
- 系统已启用登录验证

## 注意事项

1. 请确保所有必需的服务（MySQL、Redis、Chroma）都已正常启动
2. 前端开发时注意使用指定版本的 Node.js
3. 确保防火墙允许相关端口的访问
4. 如果 Chroma 服务启动失败，检查 Python 版本和依赖安装情况

## 常见问题解决

1. 如果 Chroma 安装过程中遇到依赖问题，可以尝试：
   ```bash
   pip install --upgrade pip
   pip install chromadb --no-cache-dir
   ```
2. 如果出现端口占用，可以在启动命令中指定其他端口号，同时需要修改后端的`application-langchain4j.yml`文件
