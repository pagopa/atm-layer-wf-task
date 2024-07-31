package it.pagopa.atmlayer.wf.task.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.client.bean.Token;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey = "mil-token-api")
public interface MilAuthClient {
	
	@POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Token> getTokenFromMil(@NotNull @HeaderParam("Content_Type") String contentType,
                               @NotNull @HeaderParam("RequestId") String requestId,
                               @NotNull @HeaderParam("AcquirerId") String acquirerId,
                               @NotNull @HeaderParam("Channel") String channel,
                               @NotNull @HeaderParam("TerminalId") String terminalId,
                               @NotNull String body);

}
