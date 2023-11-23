package it.pagopa.atmlayer.wf.task.bean.outcome;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@RegisterForReflection
public class OutcomeResponse {

    private String result;

    private String description;

    @JsonIgnore
    private OutcomeEnum outcomeEnum;

    public OutcomeResponse(OutcomeEnum outcomeEnum) {
        this.result = outcomeEnum.getValue();
        this.description = outcomeEnum.getDescription();
        this.outcomeEnum = outcomeEnum;
    }

    public void setOutcomeEnum(OutcomeEnum outcomeEnum) {
        this.result = outcomeEnum.getValue();
        this.description = outcomeEnum.getDescription();
        this.outcomeEnum = outcomeEnum;
    }

    public OutcomeEnum getOutcomeEnum() {
        return outcomeEnum;
    }

}
