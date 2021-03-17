package de.essquare.driftbottle.infrastructure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterAttributes;

public class DriftbottleS3Stack extends Stack {

    // TODO check this out: https://github.com/aws-samples/aws-cdk-examples/blob/master/java/static-site/src/main/java/software/amazon/awscdk/examples/StaticSiteStack.java

    public DriftbottleS3Stack(final Construct scope, final String id) throws IOException {
        this(scope, id, null);
    }

    public DriftbottleS3Stack(final Construct scope, final String id, final StackProps props) throws IOException {
        super(scope, id, props);

        Bucket frontendBucket = createBucket();
        writeConfig(); // need to do this before copying frontend to bucket
        copyFrontendToBucket(frontendBucket);
    }

    private Bucket createBucket() {
        return Bucket.Builder.create(this, "FrontendBucket")
                             .publicReadAccess(true)
                             .websiteIndexDocument("index.html")
                             .autoDeleteObjects(true)
                             .removalPolicy(RemovalPolicy.DESTROY)
                             .build();
    }

    private void writeConfig() throws IOException {
        StringParameterAttributes stringParameterAttributes = StringParameterAttributes.builder()
                                                                                       .parameterName(DriftbottleCognitoStack.SSM_COGNITO_USERPOOL_ID_NAME)
                                                                                       .build();
        String userpoolId = StringParameter.fromStringParameterAttributes(this, DriftbottleCognitoStack.USER_POOL_ID_SSM_PARAMETER, stringParameterAttributes).getStringValue();
        Files.writeString(Path.of("../frontend/dist/config.js"), "{userpoolId:\"" + userpoolId + "\"}");
    }

    private void copyFrontendToBucket(Bucket frontendBucket) {
        BucketDeployment.Builder.create(this, "DeployFrontend")
                                .sources(List.of(Source.asset("../frontend/dist")))
                                .destinationBucket(frontendBucket)
                                .destinationKeyPrefix("web/static")
                                .build();
    }
}
