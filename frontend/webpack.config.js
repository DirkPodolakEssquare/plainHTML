const path = require('path')
const webpack = require('webpack')
const CopyWebpackPlugin = require('copy-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

module.exports = {
  mode: 'development',
  // mode: 'production',
  devtool: 'source-map',
  entry: './src/app.js',


  output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'dist'),
    publicPath: '',
    // plugins: [
    //   new HtmlWebpackPlugin({
    //     "template": "html!./index.html"
    //   }),
    // ]
  },
  // output: {
  //   filename: '[name].bundle.js',
  //   path: path.resolve(__dirname, 'dist')
  // },
  //





  // optimization: {
  //   usedExports: true,
  //   minimize: true
  // },
  module: {
    rules: [
      {
        test: /\.js$/i,
        enforce: 'pre',
        use: ['source-map-loader'],
        exclude: [path.resolve(__dirname, "myconfig.js")]
      }, {
        test: /\.js$/i,
        exclude: /node_modules/
      },
      {
        test: /\.css$/i,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader'
        ]
      }
    ]
  },
  devServer: {
    contentBase: './dist',
    overlay: true,
    hot: true
  },
  plugins: [
    // new webpack.optimize.OccurrenceOrderPlugin(),
    // new webpack.DefinePlugin({
    //   'process.env': {
    //     'NODE_ENV': JSON.stringify('production')
    //   }
    // }),
    new CopyWebpackPlugin({
      patterns: ['*.html', 'images/*.png']
    }),
    // new webpack.HotModuleReplacementPlugin(),
    new MiniCssExtractPlugin({
      filename: 'css/driftbottle.css'
    })
  ]
}

// var path = require('path');
// var webpack = require('webpack');
//
// module.exports = {
//   devtool: 'source-map',
//   entry: [
//     './src/index'
//   ],
//   output: {
//     path: path.join(__dirname, 'dist'),
//     filename: 'bundle.js',
//     publicPath: '/static/'
//   },
//   plugins: [
//     new webpack.optimize.OccurenceOrderPlugin(),
//     new webpack.DefinePlugin({
//       'process.env': {
//         'NODE_ENV': JSON.stringify('production')
//       }
//     }),
//     new webpack.optimize.UglifyJsPlugin({
//       compressor: {
//         warnings: false
//       }
//     })
//   ],
//   module: {
//     loaders: [{
//       test: /\.js$/,
//       loaders: ['babel'],
//       include: path.join(__dirname, 'src')
//     }]
//   }
// };





// module.exports = {
//   output: {
//     filename: "app/[name]-[hash].js",
//     path: "dist",
//     publicPath: ""
//   },
//   plugins: [
//     new HtmlWebpackPlugin({
//       "template": "html!./index.html"
//     }),
//   ],
// }