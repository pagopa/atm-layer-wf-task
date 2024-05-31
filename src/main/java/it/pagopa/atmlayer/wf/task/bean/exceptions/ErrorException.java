package it.pagopa.atmlayer.wf.task.bean.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ErrorException extends WebApplicationException {

    public ErrorException(ErrorEnum error) {
        super(Response.status(error.getStatus()).entity(buildError(error, "")).build());
    }
    
    public ErrorException(ErrorEnum error, String additionalDescription) {
        super(Response.status(error.getStatus()).entity(buildError(error, additionalDescription)).build());
    }
    
    private static ErrorResponse buildError(ErrorEnum error, String additionalDescription) {
        return ErrorResponse.builder()
        .status(error.getStatus())
        .description(error.getDescription( ) + additionalDescription)
        .errorCode(error.getErrorCode()).build();        
    } 

}
