# plain HTML mini project template

# prerequisits
You need an installed AWS CDK CLI.
You need an AWS account and a (non-root) user.
You need a configured local environment (i.e. your .aws folder contains a valid credentials file).

# deployment
Execute buildAndDeploy.sh.

# components
This deploys a Cloudformation stack for Cognito, a second one for the frontend and a third one for the backend.

# internals
The frontend is a plain HTML webpage and stored in an S3 bucket that is configured for web access. It contains Bootstrap 5 and is packaged by Webpack. For talking to Cognito it uses AWS Amplify (-core and -auth only).
The frontend needs a configuration that lists the Cognito userpool id. This configuration is created and uploaded to the S3 buckt via an AWS Lambda function and managed by the custom Construct "FrontendConfigCreator".
If you have installed the AWS CLI, you can quickly deploy a new frontend version by calling "aws s3 mv filename.txt s3://bucket-name" after "yarn build" in the dist folder.

The backend is a Spring Boot application and runs in Elastic Beanstalk. This leads to another stack being created for handling the ElasticBeanstalk-internal ressources.
The backend's resources (here: a DynamoDB table) are managed from within the Elastic Beanstalk application (see .ebextensions folder).
If you have installed the EB CLI, you can quickly deploy a new backend version by calling "eb deploy".