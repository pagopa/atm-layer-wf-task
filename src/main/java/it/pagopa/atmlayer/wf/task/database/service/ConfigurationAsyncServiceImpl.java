package it.pagopa.atmlayer.wf.task.database.service;

import io.quarkus.amazon.dynamodb.enhanced.runtime.NamedDynamoDbTable;
import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.database.entity.Configuration;
import it.pagopa.atmlayer.wf.task.database.service.contract.ConfigurationService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@ApplicationScoped
public class ConfigurationAsyncServiceImpl extends ConfigurationService{

    @Inject
    @NamedDynamoDbTable(CONFIGURATION_TABLE_NAME)
    private DynamoDbAsyncTable<Configuration> configurationTable;

    public Uni<Configuration> get(String name) {
        Key partitionKey = Key.builder().partitionValue(name).build();
        return Uni.createFrom().completionStage(() -> configurationTable.getItem(partitionKey));
    }

}
