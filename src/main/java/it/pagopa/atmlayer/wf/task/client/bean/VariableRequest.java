package it.pagopa.atmlayer.wf.task.client.bean;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariableRequest {

    private String taskId;

    private List<String> buttons;

    private List<String> variables;

}
