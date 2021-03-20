package de.essquare.driftbottle.infrastructure;

import java.io.IOException;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public class DriftbottleApp {

    private static final String ESSQUARE_ACCOUNT = "198565159813";
    private static final String ESSQUARE_REGION = "eu-central-1";
    private static final Environment ESSQUARE_ENVIRONMENT = Environment.builder()
                                                                       .account(ESSQUARE_ACCOUNT)
                                                                       .region(ESSQUARE_REGION)
                                                                       .build();

    public static void main(final String[] args) throws IOException {
        App app = new App();

        DriftbottleCognitoStack driftbottleCognitoStack = new DriftbottleCognitoStack(app,
                                                                                      "DriftbottleCognitoStack",
                                                                                      StackProps.builder()
                                                                                                .env(ESSQUARE_ENVIRONMENT)
                                                                                                .build());

        new DriftbottleS3Stack(app,
                               "DriftbottleFrontendS3Stack",
                               StackProps.builder()
                                         .env(ESSQUARE_ENVIRONMENT)
                                         .build(),
                               driftbottleCognitoStack.getUserPool().getUserPoolId(),
                               driftbottleCognitoStack.getUserPoolClient().getUserPoolClientId(),
                               ESSQUARE_REGION);

        app.synth();
    }
}
