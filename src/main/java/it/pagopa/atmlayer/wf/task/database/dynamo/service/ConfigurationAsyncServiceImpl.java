package it.pagopa.atmlayer.wf.task.database.dynamo.service;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.database.dynamo.entity.Configuration;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.contract.ConfigurationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@ApplicationScoped
public class ConfigurationAsyncServiceImpl extends ConfigurationService{

    @Inject
    DynamoDbAsyncClient dynamoDB;

    public Uni<Configuration> get(String name) {
        return Uni.createFrom().completionStage(() -> dynamoDB.getItem(getRequest(name)))
                .onItem().transform(resp -> Configuration.from(resp.item()));
    }

}
