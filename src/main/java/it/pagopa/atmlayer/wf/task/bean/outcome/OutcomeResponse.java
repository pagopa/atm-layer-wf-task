package it.pagopa.atmlayer.wf.task.bean.outcome;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

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

    @Schema(required = true, description = "Risultato dell'operazione")
    private String result;

    @Schema(required = true, description = "Descrizione dell'esito dell'operazione")
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
