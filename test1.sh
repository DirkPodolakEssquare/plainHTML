#!/bin/bash

region=testregion
userpoolid=testuserpoolid
userpoolclientid=testuserpoolclientid

# build frontend
cd frontend
echo "backup myConfig.js"
mv myConfig.js myConfig.bak.js
echo "copy template to myConfig.js"
cp configTemplate.js myConfig.js
echo "fix placeholders"
sed -i "s/###REGION###/$region/g" myConfig.js
sed -i "s/###USERPOOLID###/$userpoolid/g" myConfig.js
sed -i "s/###USERPOOLCLIENTID###/$userpoolclientid/g" myConfig.js

echo "clean"
yarn clean
echo "build"
yarn build

echo "delete myConfig.js"
rm myConfig.js
echo "regenerate backed up myConfig.js"
mv myConfig.bak.js myConfig.js
cd ..
