package it.pagopa.atmlayer.wf.task.resource.interceptors;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.slf4j.MDC;

import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Utility;
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

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (requestContext.getUriInfo().getPath().startsWith("/api/v1")) {
            String transactionId = null;
            MultivaluedMap<String, String> pathParameters = requestContext.getUriInfo().getPathParameters();
            if (pathParameters != null && pathParameters.get(Constants.TRANSACTION_ID_PATH_PARAM_NAME) != null) {
                transactionId = pathParameters.get(Constants.TRANSACTION_ID_PATH_PARAM_NAME).get(0);
            }
            byte[] entity = requestContext.getEntityStream().readAllBytes();
            State state = Utility.getObject(new String(entity), State.class);
            if (transactionId == null) {
                transactionId = Utility.generateTransactionId(state);
            }           
            state.setTransactionId(transactionId);           
           
            MDC.put(Constants.TRANSACTION_ID_LOG_CONFIGURATION, transactionId);
            log.info("============== REQUEST ==============");
            if (pathParameters != null) {
                log.info("PATH PARAMS: {}", pathParameters);
            }
            log.info("HEADERS: {}", requestContext.getHeaders());
            log.info("METHOD: {}", requestContext.getMethod());

            log.info("BODY: {}", state);

            requestContext.setEntityStream(new ByteArrayInputStream(Utility.setTransactionIdInJson(entity,transactionId)));
          
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
            MDC.remove(Constants.TRANSACTION_ID_LOG_CONFIGURATION);
        }
    }

}