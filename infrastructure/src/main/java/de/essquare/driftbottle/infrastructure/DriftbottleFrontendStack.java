package de.essquare.driftbottle.infrastructure;

import java.util.List;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.BucketDeploymentProps;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;

public class DriftbottleFrontendStack extends Stack {

    // TODO check this out: https://github.com/aws-samples/aws-cdk-examples/blob/master/java/static-site/src/main/java/software/amazon/awscdk/examples/StaticSiteStack.java

    public static final String FRONTEND_BUCKET_ID = "DriftbottleFrontendBucketId";
    public static final String FRONTEND_BUCKET_NAME = "DriftbottleFrontendBucketName";
    public static final String FRONTEND_BUCKET_DEPLOYMENT_ID = "DriftbottleFrontendBucketDeploymentId";
    public static final String SSM_PARAMETER_POSTFIX = "SSMParameter";
    public static final String OUTPUT_PARAMETER_POSTFIX = "OutputParameter";

    private Bucket frontendBucket;

    public DriftbottleFrontendStack(Construct scope,
                                    String id,
                                    StackProps props,
                                    String userpoolId,
                                    String userpoolClientId,
                                    String region) {
        super(scope, id, props);

        createBucket();
        putFrontendCodeToBucket(userpoolId, userpoolClientId, region);
    }

    private void createBucket() {
        // create frontend bucket
        frontendBucket = Bucket.Builder.create(this, FRONTEND_BUCKET_ID)
                                       .publicReadAccess(true)
                                       .websiteIndexDocument("index.html")
                                       .autoDeleteObjects(true)
                                       .removalPolicy(RemovalPolicy.DESTROY)
                                       .build();

        // store frontend bucket name in SSM parameter storage
        StringParameterProps stringParameterProps = StringParameterProps.builder()
                                                                        .parameterName(FRONTEND_BUCKET_NAME + SSM_PARAMETER_POSTFIX)
                                                                        .stringValue(frontendBucket.getBucketName())
                                                                        .build();
        new StringParameter(this, FRONTEND_BUCKET_NAME + SSM_PARAMETER_POSTFIX, stringParameterProps);

        // output the frontend bucket name
        CfnOutputProps cfnOutputProps = CfnOutputProps.builder()
                                                      .value(frontendBucket.getBucketName())
                                                      .build();
        new CfnOutput(this, FRONTEND_BUCKET_NAME + OUTPUT_PARAMETER_POSTFIX, cfnOutputProps);
    }

    private void putFrontendCodeToBucket(String userpoolId, String userpoolClientId, String region) {
        // deploy frontend sourcecode from dist folder to the S3 bucket
        BucketDeploymentProps bucketDeploymentProps = BucketDeploymentProps.builder()
                                                                           .destinationBucket(frontendBucket)
                                                                           .sources(List.of(Source.asset("../frontend/dist")))
                                                                           .build();
        new BucketDeployment(this, FRONTEND_BUCKET_DEPLOYMENT_ID, bucketDeploymentProps);
    }
}
