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

    public static void main(String[] args) throws IOException {
        App app = new App();

        StackProps stackProps = StackProps.builder()
                                          .env(ESSQUARE_ENVIRONMENT)
                                          .build();

        new DriftbottleCognitoStack(app, "DriftbottleCognitoStack", stackProps);
        new DriftbottleBackendStack(app, "DriftbottleBackendStack", stackProps);
        new DriftbottleFrontendStack(app, "DriftbottleFrontendStack", stackProps);

        app.synth();
    }
}
