package de.essquare.driftbottle.infrastructure;

import java.util.List;

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

    public DriftbottleBackendStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public DriftbottleBackendStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // Construct an S3 asset from the ZIP located from directory up.
        AssetProps assetProps = AssetProps.builder()
                                          .path("../backend/target/backend-1.0-SNAPSHOT.jar")
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
                                                                                                                     .value("t3.small")
                                                                                                                     .build();
        CfnEnvironment.OptionSettingProperty iamInstanceProfileOptionSettingProperty = CfnEnvironment.OptionSettingProperty.builder()
                                                                                                                           .namespace("aws:autoscaling:launchconfiguration")
                                                                                                                           .optionName("IamInstanceProfile")
                                                                                                                           .value("aws-elasticbeanstalk-ec2-role")
                                                                                                                           .build();
        // Create an app version from the S3 asset defined above
        // The S3 "putObject" will occur first before CF generates the template
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
                                                                     .build();
        new CfnEnvironment(this, "DriftbottleEnvironmentId", cfnEnvironmentProps);

        cfnApplicationVersion.addDependsOn(application); // Also very important - make sure that `app` exists before creating an app version
    }

}
