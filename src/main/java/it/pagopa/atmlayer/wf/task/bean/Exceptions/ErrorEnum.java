package it.pagopa.atmlayer.wf.task.bean.exceptions;

import lombok.Getter;

@Getter
public enum ErrorEnum {

    GENERIC_ERROR("GENERIC_ERROR", "A generic error occurred", 500),
    MISSING_TASK_ID("TASK_ID", "Task id is null or empty", 400),
    INVALID_TRANSACTION_ID("MALFORMED_TRANSACTION_ID", "Transaction id not valid", 400),
    GET_TASKS_ERROR("TASK_ERROR", "Error retrieve next step", 500),
    GET_VARIABLES_ERROR("VARIABLE_ERROR", "Error retrieve variables on next step", 500),
    DESIGN_ERROR("ERROR ", "Error in function design", 204),
    CONNECTION_PROBLEM("CONNECTION_PROBLEM", "Could not connect to other microservices", 500);

    private final String errorCode;
    private final String description;
    private final int status;

    ErrorEnum(String errorCode, String description, int status) {
        this.errorCode = errorCode;
        this.status = status;
        this.description = description;
    }

}
