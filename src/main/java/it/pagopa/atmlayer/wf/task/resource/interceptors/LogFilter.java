package it.pagopa.atmlayer.wf.task.resource.interceptors;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.MDC;

import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.client.MilAuthRestClient;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String TRANSACTION_ID_LOG_CONFIGURATION = "transactionId";

    private static final String TRANSACTION_ID_PATH_PARAM_NAME = "transactionId";
    
    @RestClient
    MilAuthRestClient milAuthRestClient;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (requestContext.getUriInfo().getPath().startsWith("/api/v1")) {
            String transactionId = null;
            MultivaluedMap<String, String> pathParameters = requestContext.getUriInfo().getPathParameters();
            if (pathParameters != null && pathParameters.get(TRANSACTION_ID_PATH_PARAM_NAME) != null) {
                transactionId = pathParameters.get(TRANSACTION_ID_PATH_PARAM_NAME).get(0);
            }
            byte[] entity = requestContext.getEntityStream().readAllBytes();
            State state = Utility.getObject(new String(entity), State.class);
            if (transactionId == null) {
                transactionId = Utility.generateTransactionId(state);
            }           
            state.setTransactionId(transactionId);
            
            RestResponse<String> restTaskResponse = null;
            try {
                Device device = state.getDevice();
                log.info("Calling milAuth get Token.");
                restTaskResponse = milAuthRestClient.getToken(
                        device.getBankId(), 
                        device.getChannel().name(), 
                        state.getFiscalCode(), 
                        device.getTerminalId(),
                        transactionId);
                log.info("milAuth get Token response: [{}]",restTaskResponse.getEntity());
            } catch (WebApplicationException e) {
                log.error("Error calling milAuth get Token service", e);               
                throw new ErrorException(ErrorEnum.CONNECTION_PROBLEM);
            }
            
            entity = Utility.getJson(state).getBytes();
            MDC.put(TRANSACTION_ID_LOG_CONFIGURATION, transactionId);
            log.info("============== REQUEST ==============");
            if (pathParameters != null) {
                log.info("PATH PARAMS: {}", pathParameters);
            }
            log.info("HEADERS: {}", requestContext.getHeaders());
            log.info("METHOD: {}", requestContext.getMethod());

            log.info("BODY: {}", new String(entity));

            requestContext.setEntityStream(new ByteArrayInputStream(entity));
            log.info("============== REQUEST ==============");
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (requestContext.getUriInfo().getPath().startsWith("/api/v1")) {
            log.info("============== RESPONSE ==============");
            log.info("Response: Status: {}", responseContext.getStatus());
            if (responseContext.getEntity() != null) {
                log.info("Body: {}", Utility.getObscuredJson(responseContext.getEntity()));
            }
            log.info("============== RESPONSE ==============");
            MDC.remove(TRANSACTION_ID_LOG_CONFIGURATION);
        }
    }

}