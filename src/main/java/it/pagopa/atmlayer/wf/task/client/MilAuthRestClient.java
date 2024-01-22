package it.pagopa.atmlayer.wf.task.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.client.bean.TokenResponse;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "mil-auth-rest-client")
public interface MilAuthRestClient {
    
    @POST
    @Path("/token")
    RestResponse<TokenResponse> getToken(
            @HeaderParam("AcquirerId") String bankId,
            @HeaderParam("Channel") String channel,
            @HeaderParam("FiscalCode") String fiscalCode,
            @HeaderParam("TerminalId") String terminalId,
            @HeaderParam("TransactionId") String transactionId);
}