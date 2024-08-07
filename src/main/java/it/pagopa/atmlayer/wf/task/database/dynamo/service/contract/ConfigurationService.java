package it.pagopa.atmlayer.wf.task.database.dynamo.service.contract;

import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;


public abstract class ConfigurationService {

    public static final String CONFIGURATION_ID_COL = "id";
    public static final String CONFIGURATION_ENABLED_COL = "enabled";
    public static final String CONFIGURATION_TABLE_NAME = "pagopa-atm-layer-wf-task-trace-logs";

    public static final String TRACING = "tracing";


    protected GetItemRequest getRequest(String name) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(CONFIGURATION_ID_COL, AttributeValue.builder().s(name).build());

        return GetItemRequest.builder()
                .tableName(CONFIGURATION_TABLE_NAME)
                .key(key)
                .attributesToGet(CONFIGURATION_ID_COL, CONFIGURATION_ENABLED_COL)
                .build();
    }
}
