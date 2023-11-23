package it.pagopa.atmlayer.wf.task.bean.outcome;

import lombok.Getter;

@Getter
public enum OutcomeEnum {

    OK("OK", "The operation completed successfully"),
    PROCESSING("PROCESSING", "Process still running, retry later");

    private final String value;
    private final String description;

    OutcomeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

}
