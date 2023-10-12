package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.List;

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
public class TaskResponse {

    @JsonProperty(value = "tasks")
    private List<Task> tasks;

    @JsonProperty(value = "transactionId")
    private String transactionId;

}