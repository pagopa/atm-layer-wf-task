package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class TaskRequest {

    @JsonProperty(value = "taskId")
    private String taskId;

    @JsonProperty(value = "transactionId")
    private String transactionId;

    @JsonProperty(value = "variables")
    private Map<String, Object> variables;

    @JsonProperty(value = "deviceInfo")
    private DeviceInfo deviceInfo;

}