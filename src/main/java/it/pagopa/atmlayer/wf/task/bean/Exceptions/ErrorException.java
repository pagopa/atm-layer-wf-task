package it.pagopa.atmlayer.wf.task.bean.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ErrorException extends WebApplicationException {

    public ErrorException(ErrorBean error) {
        super(Response.status(error.getStatus()).entity(ErrorResponse.builder()
                .status(error.getStatus())
                .description(error.getDescription())
                .errorCode(error.getErrorCode()).build()).build());
    }

}
