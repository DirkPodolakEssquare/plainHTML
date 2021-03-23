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
mvn package
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

# deploy frontend
cd infrastructure
cdk synth DriftbottleFrontendStack
cdk bootstrap
cdk deploy --require-approval never -j DriftbottleFrontendStack --outputs-file outputsFrontend.json
cd ..
