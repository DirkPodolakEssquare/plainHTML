#!/bin/bash

# build backend
cd backend
mvn package cd..

# build frontend
cd frontend
yarn build
cd ..

# deployment
cdk synth
cdk bootstrap
#cdk deploy --require-approval never -j
