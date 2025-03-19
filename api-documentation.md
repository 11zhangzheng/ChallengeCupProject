# Langchain 知识库接口文档

## 基础信息

- 基础路径: `/api/chat`
- 响应格式: JSON/String
- 服务器地址: 根据实际部署环境配置

## 详细接口说明

### 1. 上传文档到知识库

将文档上传到知识库中进行处理。

- **接口路径**: `/api/chat/upload`
- **请求方法**: POST
- **Content-Type**: multipart/form-data

#### 请求参数

| 参数名  | 类型   | 是否必须 | 描述     |
|------|------|------|--------|
| file | File | 是    | 要上传的文件 |

#### 支持的文件格式

- txt (文本文件)
- pdf (PDF文档)
- doc/docx (Word文档)
- ppt/pptx (PowerPoint文档)
- xls/xlsx (Excel文档)

#### 请求示例

```http
POST /api/chat/upload HTTP/1.1
Host: your-server.com
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW

------WebKitFormBoundary7MA4YWxkTrZu0gW
Content-Disposition: form-data; name="file"; filename="document.pdf"
Content-Type: application/pdf

[文件二进制内容]
------WebKitFormBoundary7MA4YWxkTrZu0gW--
```

#### 成功响应

```json
{
    "code": 200,
    "message": "文档上传成功"
}
```

#### 错误响应

```json
{
    "code": 400,
    "message": "目前只支持txt, pdf, doc, docx, ppt, pptx, xls, xlsx格式文件"
}
```

或

```json
{
    "code": 400,
    "message": "文档内容为空"
}
```

### 2. 知识库问答接口

向知识库提问并获取AI回答。

- **接口路径**: `/api/chat/question`
- **请求方法**: POST
- **Content-Type**: application/json

#### 请求参数

| 参数名      | 类型     | 是否必须 | 描述   |
|----------|--------|------|------|
| question | String | 是    | 问题内容 |

#### 请求示例

```http
POST /api/chat/question HTTP/1.1
Host: your-server.com
Content-Type: application/json

{
    "question": "这个产品的主要功能是什么？"
}
```

#### 成功响应示例

```json
{
    "code": 200,
    "data": "根据文档内容，该产品的主要功能包括：[具体回答内容]",
    "message": "success"
}
```

### 3. 网页URL上传接口

通过URL将网页内容添加到知识库。

- **接口路径**: `/api/chat/url`
- **请求方法**: POST
- **Content-Type**: application/json

#### 请求参数

| 参数名 | 类型     | 是否必须 | 描述      |
|-----|--------|------|---------|
| url | String | 是    | 网页URL地址 |

#### 请求示例

```http
POST /api/chat/url HTTP/1.1
Host: your-server.com
Content-Type: application/json

{
    "url": "https://example.com/article"
}
```

#### 成功响应示例

```json
{
    "code": 200,
    "message": "网页内容加载成功"
}
```

#### 错误响应示例

```json
{
    "code": 400,
    "message": "网页加载失败: 无法访问指定URL"
}
```

## 接口调用注意事项

### 文档上传

1. 文件大小限制：建议单个文件不超过10MB
2. 文件格式要求：必须是支持的文件类型之一
3. 上传后处理时间：大文件可能需要较长处理时间

### 问答接口

1. 问题长度：建议不超过500字
2. 提问频率：建议间隔1-2秒
3. 建议在文档上传完成后等待几秒再进行提问

### URL上传

1. URL必须是可公开访问的网址
2. URL必须包含http://或https://
3. 网页内容必须是文本形式
4. 建议URL指向具体的文章页面而不是首页

## 错误码对照表

| 状态码 | 说明      | 处理建议     |
|-----|---------|----------|
| 200 | 请求成功    | 正常处理     |
| 400 | 请求参数错误  | 检查请求参数格式 |
| 401 | 未授权     | 检查认证信息   |
| 403 | 禁止访问    | 检查权限     |
| 404 | 资源不存在   | 检查请求路径   |
| 500 | 服务器内部错误 | 联系后端开发人员 |

## 开发环境信息

- 测试环境URL：[待定]
- 生产环境URL：[待定]
- API版本：v1.0

## 调试建议

1. 使用Postman等工具进行接口测试
2. 开发时先使用小文件测试
3. 注意检查响应状态码和错误信息
4. 遇到问题时保留请求和响应日志
