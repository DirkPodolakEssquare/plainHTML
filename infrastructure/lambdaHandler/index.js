// exports.handler = async (event) => {
//   const ssm = new (require('aws-sdk/clients/ssm'))();
//
//   const data = await ssm.getParameters({
//     Names: ["DriftbottleUserpoolIdSSMParameter"]
//   }).promise();
//
//   return {
//     statusCode: 200,
//     body: data,
//   };
// }



exports.handler = async (event) => {
  const ssm = new (require('aws-sdk/clients/ssm'))()
  // var params = {
  //   Name: '/driftbottle/test/userName',
  //   Value: 'new username',
  //   Overwrite: true,
  //   Type: 'String'
  // };
  //
  // // Update existing userName
  // await ssm.putParameter(params).promise();

  // const earlyData = await ssm.StringParameter.valueFromLookup(this, 'DriftbottleUserpoolIdSSMParameter');

  const data= await ssm.getParameters({
    Names: [`DriftbottleUserpoolIdSSMParameter`]
  }).promise();

  console.log(earlyData)
  console.log(data)

  const response = {
    statusCode: 200,
    body: data,
  };
  return response;
};