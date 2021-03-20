package de.essquare.driftbottle.infrastructure;

import java.util.HashMap;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.NotificationKeyFilter;
import software.amazon.awscdk.services.s3.notifications.LambdaDestination;

public class FrontendConfigCreator extends Construct {

    public FrontendConfigCreator(Construct scope, String id, FrontendConfigCreatorProps frontendConfigCreatorProps) {
        super(scope, id);

        // TODO: can we use a Java handler here?
        Function handler = Function.Builder.create(this, "FrontendConfigCreatorHandler")
                                           .runtime(Runtime.NODEJS_14_X)
                                           .code(Code.fromAsset("lambdaHandler"))
                                           .handler("writeConfig.main")
                                           .environment(new HashMap<String, String>() {{
                                               put("BUCKET", frontendConfigCreatorProps.getBucket().getBucketName());
                                               put("USERPOOL_ID", frontendConfigCreatorProps.getUserpoolId());
                                               put("USERPOOL_CLIENT_ID", frontendConfigCreatorProps.getUserpoolClientId());
                                               put("REGION", frontendConfigCreatorProps.getRegion());
                                           }}).build();

        frontendConfigCreatorProps.getBucket().grantReadWrite(handler);

        NotificationKeyFilter notificationKeyFilter = NotificationKeyFilter.builder()
                                                                           .suffix("index.html")
                                                                           .build();
        frontendConfigCreatorProps.getBucket().addEventNotification(EventType.OBJECT_CREATED, new LambdaDestination(handler), notificationKeyFilter);

// TODO: should we stick to the "S3 OBJECT_CREATE" trigger or should we use something else?
//        RestApi api = RestApi.Builder.create(this, "Widgets-API")
//                                     .restApiName("Widget Service").description("This service services widgets.")
//                                     .build();
//
//        LambdaIntegration getWidgetsIntegration = LambdaIntegration.Builder.create(handler)
//                                                                           .requestTemplates(new HashMap<String, String>() {{
//                                                                               put("application/json", "{ \"statusCode\": \"200\" }");
//                                                                           }}).build();
//
//        api.getRoot().addMethod("GET", getWidgetsIntegration);
    }
}