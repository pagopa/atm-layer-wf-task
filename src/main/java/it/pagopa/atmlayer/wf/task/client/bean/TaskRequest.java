package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
@JsonInclude(Include.NON_NULL)
public class TaskRequest {

    private String functionId;

    private String taskId;

    private String transactionId;

    private DeviceInfo deviceInfo;

    private Map<String, Object> variables;

}