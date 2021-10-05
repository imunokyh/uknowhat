const path = require("path");
module.exports = {
  outputDir: path.resolve(__dirname, "../src/main/resources/static"),
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:9080'      
      },
      '/stomp': {
        target: 'http://localhost:9080'
      }
    }
  }
}