package de.essquare.driftbottle.infrastructure;

import java.util.List;

import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.cognito.AuthFlow;
import software.amazon.awscdk.services.cognito.AutoVerifiedAttrs;
import software.amazon.awscdk.services.cognito.CfnUserPool;
import software.amazon.awscdk.services.cognito.CfnUserPool.AccountRecoverySettingProperty;
import software.amazon.awscdk.services.cognito.CfnUserPool.RecoveryOptionProperty;
import software.amazon.awscdk.services.cognito.CognitoDomainOptions;
import software.amazon.awscdk.services.cognito.IUserPool;
import software.amazon.awscdk.services.cognito.Mfa;
import software.amazon.awscdk.services.cognito.OAuthScope;
import software.amazon.awscdk.services.cognito.OAuthSettings;
import software.amazon.awscdk.services.cognito.PasswordPolicy;
import software.amazon.awscdk.services.cognito.ResourceServerScope;
import software.amazon.awscdk.services.cognito.ResourceServerScopeProps;
import software.amazon.awscdk.services.cognito.SignInAliases;
import software.amazon.awscdk.services.cognito.StandardAttribute;
import software.amazon.awscdk.services.cognito.StandardAttributes;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.UserPoolClientProps;
import software.amazon.awscdk.services.cognito.UserPoolDomainOptions;
import software.amazon.awscdk.services.cognito.UserPoolProps;
import software.amazon.awscdk.services.cognito.UserPoolResourceServer;
import software.amazon.awscdk.services.cognito.UserPoolResourceServerProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;

import static de.essquare.driftbottle.infrastructure.DriftbottleApp.OUTPUT_PARAMETER_POSTFIX;
import static de.essquare.driftbottle.infrastructure.DriftbottleApp.SSM_PARAMETER_POSTFIX;

public class DriftbottleCognitoStack extends Stack {

    private static final String DOMAIN_NAME = "driftbottle";
    private static final String DOMAIN_PREFIX = "https://";
    private static final String DOMAIN_S3_PREFIX = ".s3.";
    private static final String DOMAIN_POSTFIX = ".amazonaws.com/";
    private static final String DOMAIN_PAGE = "index.html";

    private static final String USERPOOL_NAME = "DriftbottleUserpoolName";
    private static final String USERPOOL_ID = "DriftbottleUserpoolId";
    private static final String USERPOOL_CLIENT_NAME = "DriftbottleUserpoolClientName";
    private static final String USERPOOL_CLIENT_ID = "DriftbottleUserpoolClientId";
    private static final String DOMAIN_ID = "DriftbottleDomainId";
    private static final String REGION_ID = "DriftbottleRegion";
    private static final String USERPOOL_RESOURCE_SERVER_ID = "DriftbottleUserpoolResourcesServerId";

    public DriftbottleCognitoStack(Construct scope, String id, StackProps props, final Bucket frontendBucket) {
        super(scope, id, props);

        IUserPool userPool = createUserPool(DOMAIN_PREFIX + frontendBucket.getBucketName() + DOMAIN_S3_PREFIX + props.getEnv().getRegion() + DOMAIN_POSTFIX + DOMAIN_PAGE);
        createUserPoolClient(userPool);
        createUserPoolResourceServer(userPool);

        assert props.getEnv() != null;
        CfnOutputProps cfnOutputProps = CfnOutputProps.builder()
                                                      .value(props.getEnv().getRegion())
                                                      .build();
        new CfnOutput(this, REGION_ID + OUTPUT_PARAMETER_POSTFIX, cfnOutputProps);
    }

    private IUserPool createUserPool(final String bucketName) {
        // create userpool

//        userVerification: {
//            emailSubject: 'Verify your email for our awesome app!',
//                    emailBody: 'Hello {username}, Thanks for signing up to our awesome app! Your verification code is {####}',
//                    emailStyle: cognito.VerificationEmailStyle.CODE,
//                    smsMessage: 'Hello {username}, Thanks for signing up to our awesome app! Your verification code is {####}',
//        }

//        UserVerificationConfig userVerificationConfig = UserVerificationConfig.builder()
//                                                                              .emailBody("")
//                                                                              .emailStyle(VerificationEmailStyle.LINK)
//                                                                              .build();
        UserPoolProps userPoolProps = UserPoolProps.builder()
                                                   .userPoolName(USERPOOL_NAME)
                                                   .signInAliases(SignInAliases.builder()
                                                                               .email(true)
                                                                               .build())
                                                   .signInCaseSensitive(false)
                                                   .standardAttributes(StandardAttributes.builder()
                                                                                         .email(StandardAttribute.builder()
                                                                                                                 .mutable(true)
                                                                                                                 .required(true)
                                                                                                                 .build())
                                                                                         .build())
                                                   .passwordPolicy(PasswordPolicy.builder()
                                                                                 .minLength(12)
                                                                                 .requireDigits(false)
                                                                                 .requireLowercase(false)
                                                                                 .requireUppercase(false)
                                                                                 .requireSymbols(false)
                                                                                 .build())
                                                   .autoVerify(AutoVerifiedAttrs.builder()
                                                                                .email(true)
                                                                                .build())
                                                   .mfa(Mfa.OFF)
                                                   .selfSignUpEnabled(true)
//                                                   .userVerification(userVerificationConfig)
                                                   .build();

        IUserPool userPool = new UserPool(this, USERPOOL_ID, userPoolProps);
        CfnUserPool cfnUserPool = (CfnUserPool) userPool.getNode()
                                                        .getDefaultChild();
        assert cfnUserPool != null;

        CognitoDomainOptions cognitoDomainOptions = CognitoDomainOptions.builder()
                                                                        .domainPrefix(DOMAIN_NAME)
                                                                        .build();
        UserPoolDomainOptions userPoolDomainOptions = UserPoolDomainOptions.builder()
                                                                           .cognitoDomain(cognitoDomainOptions)
                                                                           .build();
        userPool.addDomain(DOMAIN_ID, userPoolDomainOptions);

        cfnUserPool.setAccountRecoverySetting(AccountRecoverySettingProperty.builder()
                                                                            .recoveryMechanisms(
                                                                                    List.of(
                                                                                            RecoveryOptionProperty.builder()
                                                                                                                  .name("verified_email")
                                                                                                                  .priority(1)
                                                                                                                  .build()))
                                                                            .build());
        cfnUserPool.setUserPoolAddOns(CfnUserPool.UserPoolAddOnsProperty.builder()
                                                                        .advancedSecurityMode("OFF")
                                                                        .build());

        // store userpool id in SSM parameter storage
        StringParameterProps stringParameterProps = StringParameterProps.builder()
                                                                        .parameterName(USERPOOL_ID + SSM_PARAMETER_POSTFIX)
                                                                        .stringValue(userPool.getUserPoolId())
                                                                        .build();
        new StringParameter(this, USERPOOL_ID + SSM_PARAMETER_POSTFIX, stringParameterProps);

        // output the userpool id
        CfnOutputProps cfnOutputProps = CfnOutputProps.builder()
                                                      .value(userPool.getUserPoolId())
                                                      .build();
        new CfnOutput(this, USERPOOL_ID + OUTPUT_PARAMETER_POSTFIX, cfnOutputProps);

        return userPool;
    }

    private void createUserPoolClient(final IUserPool userPool) {
        // create userpool client
        OAuthSettings oAuthSettings = OAuthSettings.builder()
                                                   .callbackUrls(List.of("https://driftbottle.net/index.html"))
                                                   .scopes(List.of(OAuthScope.custom("http://driftbottle.eu-central-1.elasticbeanstalk.com/api/conversation.read")))
                                                   .build();
        UserPoolClientProps userPoolClientProps = UserPoolClientProps.builder()
                                                                     .userPool(userPool)
                                                                     .generateSecret(false)
                                                                     .preventUserExistenceErrors(true)
                                                                     .userPoolClientName(USERPOOL_CLIENT_NAME)
                                                                     .authFlows(AuthFlow.builder().userSrp(true).build())
                                                                     .oAuth(oAuthSettings)
                                                                     .build();
        UserPoolClient userPoolClient = new UserPoolClient(this, USERPOOL_CLIENT_ID, userPoolClientProps);

        // store userpool client id in SSM parameter storage
        StringParameterProps stringParameterProps = StringParameterProps.builder()
                                                                        .parameterName(USERPOOL_CLIENT_ID + SSM_PARAMETER_POSTFIX)
                                                                        .stringValue(userPoolClient.getUserPoolClientId())
                                                                        .build();
        new StringParameter(this, USERPOOL_CLIENT_ID + SSM_PARAMETER_POSTFIX, stringParameterProps);

        // output the userpool client id
        CfnOutputProps cfnOutputProps = CfnOutputProps.builder()
                                                      .value(userPoolClient.getUserPoolClientId())
                                                      .build();
        new CfnOutput(this, USERPOOL_CLIENT_ID + OUTPUT_PARAMETER_POSTFIX, cfnOutputProps);
    }

    private void createUserPoolResourceServer(final IUserPool userPool) {
        // create userpool resource server
        ResourceServerScopeProps resourceServerScopeProps = ResourceServerScopeProps.builder()
                                                                                    .scopeName("conversation.read")
                                                                                    .scopeDescription("read conversations")
                                                                                    .build();
        ResourceServerScope resourceServerScope = new ResourceServerScope(resourceServerScopeProps);
        UserPoolResourceServerProps userPoolResourceServerProps = UserPoolResourceServerProps.builder()
                                                                                             .userPool(userPool)
                                                                                             .scopes(List.of(resourceServerScope))
                                                                                             .userPoolResourceServerName("DriftbottleBackend")
                                                                                             .identifier("http://driftbottle.eu-central-1.elasticbeanstalk.com/api")
                                                                                             .build();
        UserPoolResourceServer userPoolResourceServer = new UserPoolResourceServer(this, USERPOOL_RESOURCE_SERVER_ID, userPoolResourceServerProps);

        // store userpool resource server id in SSM parameter storage
        StringParameterProps stringParameterProps = StringParameterProps.builder()
                                                                        .parameterName(USERPOOL_RESOURCE_SERVER_ID + SSM_PARAMETER_POSTFIX)
                                                                        .stringValue(userPoolResourceServer.getUserPoolResourceServerId())
                                                                        .build();
        new StringParameter(this, USERPOOL_RESOURCE_SERVER_ID + SSM_PARAMETER_POSTFIX, stringParameterProps);

        // output the userpool resource server id
        CfnOutputProps cfnOutputProps = CfnOutputProps.builder()
                                                      .value(userPoolResourceServer.getUserPoolResourceServerId())
                                                      .build();
        new CfnOutput(this, USERPOOL_RESOURCE_SERVER_ID + OUTPUT_PARAMETER_POSTFIX, cfnOutputProps);
    }
}
