package de.essquare.driftbottle.infrastructure;

import java.util.List;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplication;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplicationProps;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplicationVersion;
import software.amazon.awscdk.services.elasticbeanstalk.CfnApplicationVersionProps;
import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironment;
import software.amazon.awscdk.services.elasticbeanstalk.CfnEnvironmentProps;
import software.amazon.awscdk.services.s3.assets.Asset;
import software.amazon.awscdk.services.s3.assets.AssetProps;

public class DriftbottleBackendStack extends Stack {

    public static final String CNAME_PREFIX = "driftbottle";
    public static final String BASE_API_URL_ID = "DriftbottleBaseAPIUrl";

    public DriftbottleBackendStack(Construct scope, String id, StackProps props) {
        super(scope, id, props);

        // Construct an S3 asset from the ZIP located in the target folder.
        AssetProps assetProps = AssetProps.builder()
                                          .path("../backend/target/backend.zip")
                                          .build();
        Asset asset = new Asset(this, "DriftbottleBackendJar", assetProps);

        String appName = "Driftbottle";
        CfnApplicationProps cfnApplicationProps = CfnApplicationProps.builder()
                                                                     .applicationName(appName)
                                                                     .build();
        CfnApplication application = new CfnApplication(this, "DriftbottleApplicationId", cfnApplicationProps);

        // Example of some options which can be configured
        CfnEnvironment.OptionSettingProperty instanceTypeOptionSettingProperty = CfnEnvironment.OptionSettingProperty.builder()
                                                                                                                     .namespace("aws:autoscaling:launchconfiguration")
                                                                                                                     .optionName("InstanceType")
                                                                                                                     .value("t2.micro")
                                                                                                                     .build();
        CfnEnvironment.OptionSettingProperty iamInstanceProfileOptionSettingProperty = CfnEnvironment.OptionSettingProperty.builder()
                                                                                                                           .namespace("aws:autoscaling:launchconfiguration")
                                                                                                                           .optionName("IamInstanceProfile")
                                                                                                                           .value("aws-elasticbeanstalk-ec2-role")
                                                                                                                           .build();
        // Create an app version from the S3 asset defined above
        // The S3 "putObject" will occur first before CFN generates the template
        CfnApplicationVersion.SourceBundleProperty sourceBundleProperty = CfnApplicationVersion.SourceBundleProperty.builder()
                                                                                                                    .s3Bucket(asset.getS3BucketName())
                                                                                                                    .s3Key(asset.getS3ObjectKey())
                                                                                                                    .build();
        CfnApplicationVersionProps cfnApplicationVersionProps = CfnApplicationVersionProps.builder()
                                                                                          .applicationName(application.getApplicationName())
                                                                                          .sourceBundle(sourceBundleProperty)
                                                                                          .build();
        CfnApplicationVersion cfnApplicationVersion = new CfnApplicationVersion(this, "DriftbottleAppVersionId", cfnApplicationVersionProps);

        CfnEnvironmentProps cfnEnvironmentProps = CfnEnvironmentProps.builder()
                                                                     .environmentName("DriftbottleEnvironment")
                                                                     .applicationName(application.getApplicationName())
                                                                     .solutionStackName("64bit Amazon Linux 2 v3.1.6 running Corretto 11")
                                                                     .optionSettings(List.of(instanceTypeOptionSettingProperty, iamInstanceProfileOptionSettingProperty))
                                                                     .versionLabel(cfnApplicationVersion.getRef())
                                                                     .cnamePrefix(CNAME_PREFIX)
                                                                     .build();
        new CfnEnvironment(this, "DriftbottleEnvironmentId", cfnEnvironmentProps);

        cfnApplicationVersion.addDependsOn(application); // Also very important - make sure that `app` exists before creating an app version

        // output the base api url
        // e.g. http://driftbottle.eu-central-1.elasticbeanstalk.com/api
        CfnOutputProps cfnOutputProps = CfnOutputProps.builder()
                                                      .value("http://" + CNAME_PREFIX + "." + props.getEnv().getRegion() + ".elasticbeanstalk.com/api")
                                                      .build();
        new CfnOutput(this, BASE_API_URL_ID + DriftbottleCognitoStack.OUTPUT_PARAMETER_POSTFIX, cfnOutputProps);
    }

}
