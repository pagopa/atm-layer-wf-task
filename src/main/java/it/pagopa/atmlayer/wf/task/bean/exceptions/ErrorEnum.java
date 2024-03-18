package it.pagopa.atmlayer.wf.task.bean.exceptions;

import lombok.Getter;

@Getter
public enum ErrorEnum {

    GENERIC_ERROR("GENERIC_ERROR", "A generic error occurred", 500),
    INVALID_TRANSACTION_ID("MALFORMED_TRANSACTION_ID", "Transaction id not valid", 400),
    GET_TASKS_ERROR("TASK_ERROR", "Error retrieve next step", 500),
    GET_VARIABLES_ERROR("VARIABLE_ERROR", "Error retrieve variables on next step", 500),
    PROCESS_ERROR("PROCESS_ERROR", "Error while running the process", 209),
    CONNECTION_PROBLEM("CONNECTION_PROBLEM", "Could not connect to other microservices", 500),
    MALFORMED_REQUEST("MALFORMED_REQUEST", "Request id not valid", 400);

    private final String errorCode;
    private String description;
    private final int status;

    ErrorEnum(String errorCode, String description, int status) {
        this.errorCode = errorCode;
        this.status = status;
        this.description = description;
    }
    
    public void setDescription(String description) {
        this.description += " - " +description;}

}
