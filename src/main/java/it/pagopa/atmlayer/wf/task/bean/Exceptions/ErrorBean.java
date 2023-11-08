package it.pagopa.atmlayer.wf.task.bean.Exceptions;

import lombok.Getter;

@Getter
public enum ErrorBean {

    GENERIC_ERROR("GENERIC_ERROR", "A generic error occurred", 500),
    MISSING_TASK_ID("TASK_ID", "Task id is null or empty", 400),
    INVALID_TRANSACTION_ID("MALFORMED_TRANSACTION_ID", "Transaction id not valid", 400),
    GET_TASKS_ERROR("TASK_ERROR", "Error retrieve next step", 502),
    GET_VARIABLES_ERROR("VARIABLE_ERROR", "Error retrieve variables on next step", 502),
    CONNECTION_PROBLEM("CONNECTION_PROBLEM", "Could not connect to other microservices", 502);

    private final String errorCode;
    private final String description;
    private final int status;

    ErrorBean(String errorCode, String description, int status) {
        this.errorCode = errorCode;
        this.status = status;
        this.description = description;
    }

}
