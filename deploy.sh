#!/bin/bash

# deploy infrastructure
cd infrastructure
cdk synth DriftbottleCognitoStack
cdk bootstrap
cdk deploy --require-approval never -j DriftbottleCognitoStack --outputs-file outputsInfrastructure.json

region=$(jq -r .DriftbottleCognitoStack.DriftbottleRegionOutputParameter outputsInfrastructure.json)
userpoolid=$(jq -r .DriftbottleCognitoStack.DriftbottleUserpoolIdOutputParameter outputsInfrastructure.json)
userpoolclientid=$(jq -r .DriftbottleCognitoStack.DriftbottleUserpoolClientIdOutputParameter outputsInfrastructure.json)
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
mv myConfig.js myConfig.bak.js
cp configTemplate.js myConfig.js
sed -i "s/###REGION###/$region/g" myConfig.js
sed -i "s/###USERPOOLID###/$userpoolid/g" myConfig.js
sed -i "s/###USERPOOLCLIENTID###/$userpoolclientid/g" myConfig.js

yarn clean
yarn build

rm myConfig.js
mv myConfig.bak.js myConfig.js
cd ..

# deploy backend
cd infrastructure
cdk synth DriftbottleBackendStack
cdk bootstrap
cdk deploy --require-approval never -j DriftbottleBackendStack --outputs-file outputsBackend.json
cd ..

# deploy frontend
cd infrastructure
cdk synth DriftbottleFrontendStack
cdk bootstrap
cdk deploy --require-approval never -j DriftbottleFrontendStack --outputs-file outputsFrontend.json
cd ..
