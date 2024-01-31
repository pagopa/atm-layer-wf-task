package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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
@JsonInclude(Include.NON_NULL)
@RegisterForReflection
public class VariableResponse {

    private Map<String, Object> buttons;

    private Map<String, Object> variables;

    @JsonAnySetter
    public void setVariable(String variableName, Object variableData) {
        variables.put(variableName, variableData);
    }
    
    @JsonAnyGetter
    public Map<String, Object> getVariables() {
        return variables;
    }

}
