package it.pagopa.atmlayer.wf.task.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.client.bean.PublicKey;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "tokenization-rest-client")
public interface TokenizationRestClient {

    @GET
    @Path("/key")
    RestResponse<PublicKey> getKey();
}