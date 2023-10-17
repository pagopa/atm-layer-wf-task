package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariableResponse {

    private Map<String, Object> buttons;

    private Map<String, Object> variables;

}
