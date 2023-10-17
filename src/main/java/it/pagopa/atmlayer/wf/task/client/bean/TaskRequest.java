package it.pagopa.atmlayer.wf.task.client.bean;

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

    private String functionId;

    private String taskId;

    private String transactionId;

    private DeviceInfo deviceInfo;

}