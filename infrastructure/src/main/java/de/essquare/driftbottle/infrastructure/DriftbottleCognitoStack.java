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
import software.amazon.awscdk.services.cognito.IUserPool;
import software.amazon.awscdk.services.cognito.IUserPoolClient;
import software.amazon.awscdk.services.cognito.Mfa;
import software.amazon.awscdk.services.cognito.PasswordPolicy;
import software.amazon.awscdk.services.cognito.SignInAliases;
import software.amazon.awscdk.services.cognito.StandardAttribute;
import software.amazon.awscdk.services.cognito.StandardAttributes;
import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.cognito.UserPoolClient;
import software.amazon.awscdk.services.cognito.UserPoolClientProps;
import software.amazon.awscdk.services.cognito.UserPoolProps;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;

public class DriftbottleCognitoStack extends Stack {

    public static final String USERPOOL_NAME = "DriftbottleUserpoolName";
    public static final String USERPOOL_ID = "DriftbottleUserpoolId";
    public static final String USERPOOL_CLIENT_NAME = "DriftbottleUserpoolClientName";
    public static final String USERPOOL_CLIENT_ID = "DriftbottleUserpoolClientId";
    public static final String SSM_PARAMETER_POSTFIX = "SSMParameter";
    public static final String OUTPUT_PARAMETER_POSTFIX = "OutputParameter";

    private IUserPool userPool;
    private IUserPoolClient userPoolClient;

    public DriftbottleCognitoStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        createUserPool();
        createUserPoolClient();
    }

    public IUserPool getUserPool() {
        return userPool;
    }

    public IUserPoolClient getUserPoolClient() {
        return userPoolClient;
    }

    private void createUserPool() {
        // create userpool
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
                                                   .build();

        userPool = new UserPool(this, USERPOOL_ID, userPoolProps);
        CfnUserPool cfnUserPool = (CfnUserPool) userPool.getNode()
                                                        .getDefaultChild();
        assert cfnUserPool != null;

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
    }

    private void createUserPoolClient() {
        // create userpool client
        UserPoolClientProps userPoolClientProps = UserPoolClientProps.builder()
                                                                     .userPool(userPool)
                                                                     .generateSecret(false)
                                                                     .preventUserExistenceErrors(true)
                                                                     .userPoolClientName(USERPOOL_CLIENT_NAME)
                                                                     .authFlows(AuthFlow.builder().userSrp(true).build())
                                                                     .build();
        userPoolClient = new UserPoolClient(this, USERPOOL_CLIENT_ID, userPoolClientProps);

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
}
