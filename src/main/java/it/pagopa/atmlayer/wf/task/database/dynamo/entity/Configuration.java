package it.pagopa.atmlayer.wf.task.database.dynamo.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.contract.ConfigurationService;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@RegisterForReflection
@DynamoDbBean
public class Configuration {
    
    private String id;

    private Boolean enabled;

    /**
     * @return String return the id
     */
    @DynamoDbPartitionKey
    @DynamoDbAttribute(ConfigurationService.CONFIGURATION_ID_COL)
    public String getId() {
        return id;
    }

    /**
     * @return Boolean return the enabled
     */
    @DynamoDbAttribute(ConfigurationService.CONFIGURATION_ID_COL)
    public Boolean isEnabled() {
        return enabled;
    }

}
