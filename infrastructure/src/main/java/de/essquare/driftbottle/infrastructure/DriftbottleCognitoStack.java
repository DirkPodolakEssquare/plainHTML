package de.essquare.driftbottle.infrastructure;

import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.cognito.AuthFlow;
import software.amazon.awscdk.services.cognito.AutoVerifiedAttrs;
import software.amazon.awscdk.services.cognito.CfnUserPool;
import software.amazon.awscdk.services.cognito.CfnUserPool.AccountRecoverySettingProperty;
import software.amazon.awscdk.services.cognito.CfnUserPool.RecoveryOptionProperty;
import software.amazon.awscdk.services.cognito.IUserPool;
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

    private static final String USER_POOL_NAME = "DriftbottleUserPoolName";
    private static final String USER_POOL_ID = "DriftbottleUserPoolId";
    private static final String USER_POOL_CLIENT_NAME = "DriftbottleUserPoolClientName";
    private static final String USER_POOL_CLIENT_ID = "DriftbottleUserPoolClientId";

    public static final String USER_POOL_ID_SSM_PARAMETER = "DriftbottleUserPoolIdSSMParameter";
    public static final String SSM_COGNITO_USERPOOL_ID_NAME = "COGNITO_USERPOOL_ID";

    public DriftbottleCognitoStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        UserPool userPool = createUserPool();
        createUserPoolClient(userPool);
        storeUserpoolIdInSSM(userPool);
    }

    private UserPool createUserPool() {
        UserPoolProps userPoolProps = UserPoolProps.builder()
                                                   .userPoolName(USER_POOL_NAME)
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

        UserPool userPool = new UserPool(this, USER_POOL_ID, userPoolProps);
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
        return userPool;
    }

    private void createUserPoolClient(UserPool userPool) {
        UserPoolClientProps userPoolClientProps = UserPoolClientProps.builder()
                                                                     .userPool(userPool)
                                                                     .generateSecret(false)
                                                                     .preventUserExistenceErrors(true)
                                                                     .userPoolClientName(USER_POOL_CLIENT_NAME)
                                                                     .authFlows(AuthFlow.builder().userSrp(true).build())
                                                                     .build();
        new UserPoolClient(this, USER_POOL_CLIENT_ID, userPoolClientProps);
    }

    private void storeUserpoolIdInSSM(final UserPool userPool) {
        String userPoolId = ((IUserPool) userPool).getUserPoolId();
        StringParameterProps stringParameterProps = StringParameterProps.builder()
                                                                        .parameterName(SSM_COGNITO_USERPOOL_ID_NAME)
                                                                        .stringValue(userPoolId)
                                                                        .build();
        new StringParameter(this, USER_POOL_ID_SSM_PARAMETER, stringParameterProps);
    }
}
