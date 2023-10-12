package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "variables")
    private Map<String, Object> variables;

    @JsonProperty(value = "form")
    private String form;

}
