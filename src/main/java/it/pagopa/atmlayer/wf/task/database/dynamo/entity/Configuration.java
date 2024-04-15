package it.pagopa.atmlayer.wf.task.database.dynamo.entity;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.contract.ConfigurationService;
import lombok.Data;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Data
@RegisterForReflection
public class Configuration {
    
    private String id;

    private Boolean enabled;

    public static Configuration from(Map<String, AttributeValue> item) {
        Configuration configuration = new Configuration();
        if (item != null && !item.isEmpty()) {
            configuration.setId(item.get(ConfigurationService.CONFIGURATION_ID_COL).s());
            configuration.setEnabled(item.get(ConfigurationService.CONFIGURATION_ENABLED_COL).bool());
        }
        return configuration;
    }

}
