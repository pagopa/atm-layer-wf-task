package it.pagopa.atmlayer.wf.task.configuration;

import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;

import static it.pagopa.atmlayer.wf.task.util.Utility.getClientId;

@Provider
@PreMatching
public class AuthorizationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext){
        String clientId = getClientId(requestContext);
        String apiKey = requestContext.getHeaders().get("x-api-key") != null ? String.valueOf(requestContext.getHeaders().get("x-api-key").getFirst()) : null;

        if (apiKey != null && !apiKey.isEmpty() && clientId !=null && !clientId.equals(apiKey)) {
            throw new ErrorException(ErrorEnum.API_KEY_MISMATCH);
        }
    }
}
