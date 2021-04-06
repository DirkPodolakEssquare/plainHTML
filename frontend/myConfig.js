// this file will not get copied into the "dist" folder by webpack, it is handled by the deploy script and excluded in the webpack.config.js

const awsConfig = {
  "aws_project_region": "eu-central-1",
  "aws_cognito_region": "eu-central-1",
  "aws_user_pools_id": "eu-central-1_1yLRgkbyK",
  "aws_user_pools_web_client_id": "6cjv2hbrolg3h07thapbq12csu",
  "oauth": {}
}

const driftbottleConfig = {
  "baseAPIUrl": "http://localhost:5000/api"
}

export { awsConfig, driftbottleConfig }

