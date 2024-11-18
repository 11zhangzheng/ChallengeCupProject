'use strict'
const path = require('path')

module.exports = {
  dev: {
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    // 跨域问题需要的代理，后端服务地址
    proxyTable: {
      '/api': {
        target: 'http://localhost:9999',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        },
        logLevel: 'debug',
        onProxyReq: function (proxyReq, req, res) {
          // 只有非文件上传请求才设置 Content-Type
          if (!req.url.includes('/chat/upload')) {
            proxyReq.setHeader('Content-Type', 'application/json');
          }
        }
      }
    },
    host: '127.0.0.1', // can be overwritten by process.env.HOST
    port: 8080, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
    autoOpenBrowser: false,
    errorOverlay: true,
    notifyOnErrors: true,
    poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-


    devtool: 'cheap-module-eval-source-map',

    cacheBusting: true,
    cssSourceMap: true
  },

  build: {
    index: path.resolve(__dirname, '../dist/index.html'),

    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',

    productionSourceMap: true,
    devtool: '#source-map',

    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static img for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],

    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report
  }
}
