package it.pagopa.atmlayer.wf.task.client.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthParameters {
	
    private String requestId;
    
    @NonNull
    private String acquirerId;
    
    @NonNull
    private String channel;
    
    @NonNull
    private String terminalId;
    
    private String fiscalCode;
    
    @NonNull
    private String transactionId;
    
}