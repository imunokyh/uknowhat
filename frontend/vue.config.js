const path = require("path");
module.exports = {
  outputDir: path.resolve(__dirname, "./tempdist"),
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:9080'      
      }
    }
  }
}