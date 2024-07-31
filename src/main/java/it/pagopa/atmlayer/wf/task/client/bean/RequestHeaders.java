package it.pagopa.atmlayer.wf.task.client.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ApplicationScoped
public class RequestHeaders {
	
    @HeaderParam("content_type")
    private String contentType;
    
    @HeaderParam("RequestId")
    private String requestId;
    
    @HeaderParam("AcquirerId")
    private String acquirerId;
    
    @HeaderParam("Channel")
    private String channel;
    
    @HeaderParam("TerminalId")
    private String terminalId;
}
