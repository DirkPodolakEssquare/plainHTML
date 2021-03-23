// this file will not get copied into the "dist" folder by webpack, this is excluded in the webpack.config.js
// the lambda "FrontendConfigCreator" (using the handler "writeConfig.js") will create this file with correct runtime data during stack deployment

const aws_config = {
  "aws_project_region": "eu-central-1",
  "aws_cognito_region": "eu-central-1",
  "aws_user_pools_id": "eu-central-1_KKjEBt79P",
  "aws_user_pools_web_client_id": "6koqd8a935a1s8otp0s4vrtuhj",
  "oauth": {}
}

export { aws_config }
