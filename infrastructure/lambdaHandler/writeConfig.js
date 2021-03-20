const AWS = require('aws-sdk')
const S3 = new AWS.S3()

const bucketName = process.env.BUCKET
const userpoolId = process.env.USERPOOL_ID
const userpoolClientId = process.env.USERPOOL_CLIENT_ID
const region = process.env.REGION

exports.main = async function(event, context) {

  const body = `
const aws_config = {
  "aws_project_region": "${region}",
  "aws_cognito_region": "${region}",
  "aws_user_pools_id": "${userpoolId}",
  "aws_user_pools_web_client_id": "${userpoolClientId}",
  "oauth": {}
}

export { aws_config }
`

  await S3.putObject({
    Bucket: bucketName,
    Key: "myconfig.js",
    Body: body,
    ContentType: 'application/json'
  }).promise()

  return {
    statusCode: 200
  }
}