package it.pagopa.atmlayer.wf.task.resource.interceptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.MDC;

import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class LogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String TRANSACTION_ID_LOG_CONFIGURATION = "transactionId";

    private static final String TRANSACTION_ID_HEADER_NAME = "transactionId";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (requestContext.getUriInfo().getPath().startsWith("/api/v1")) {
            String transactionId = null;
            log.info("============== REQUEST ==============");
            if (requestContext.getUriInfo().getPathParameters() != null) {
                if (requestContext.getUriInfo().getPathParameters().get(TRANSACTION_ID_HEADER_NAME) != null) {
                    transactionId = requestContext.getUriInfo()
                            .getPathParameters().get(TRANSACTION_ID_HEADER_NAME).get(0);
                }
                log.info("PATH PARAMS: {}",
                        requestContext.getUriInfo().getPathParameters());
            }
            byte[] entity = requestContext.getEntityStream().readAllBytes();
            if (transactionId == null) {
                transactionId = Utility.generateTransactionId(Utility.getObject(new String(entity), State.class));
            }
            State state = (State) Utility.getObject(new String(entity), State.class);
            state.setTransactionId(transactionId);
            entity = Utility.getJson(state).getBytes();
            MDC.put(TRANSACTION_ID_LOG_CONFIGURATION, transactionId);
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
                log.info("Body: {}", Utility.getJson(responseContext.getEntity()));
            }
            log.info("============== RESPONSE ==============");
            MDC.remove(TRANSACTION_ID_LOG_CONFIGURATION);
        }
    }

}