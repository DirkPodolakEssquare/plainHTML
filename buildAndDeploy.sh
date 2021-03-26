#!/bin/bash

# deploy infrastructure
cd infrastructure
cdk synth DriftbottleCognitoStack
cdk bootstrap
cdk deploy --require-approval never -j DriftbottleCognitoStack
cd ..

# build backend
cd backend

# to deploy a jar file
#mvn package

# to deploy the complete project as a zip file
zip ./target/backend.zip -r * .[^.]* -x .idea/\* target/\* .git/\* .gitignore \*.iml

cd ..

# build frontend
cd frontend
yarn clean
yarn build
cd ..

# deploy backend
cd infrastructure
cdk synth DriftbottleBackendStack
cdk bootstrap
cdk deploy --require-approval never -j DriftbottleBackendStack
cd ..

# deploy frontend
cd infrastructure
cdk synth DriftbottleFrontendStack
cdk bootstrap
cdk deploy --require-approval never -j DriftbottleFrontendStack
cd ..
