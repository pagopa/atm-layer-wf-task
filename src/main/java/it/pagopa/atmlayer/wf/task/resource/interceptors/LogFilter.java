package it.pagopa.atmlayer.wf.task.resource.interceptors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

import org.slf4j.MDC;

import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.logging.latency.LatencyTracer;
import it.pagopa.atmlayer.wf.task.util.CommonLogic;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class LogFilter extends CommonLogic implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String URI = requestContext.getUriInfo().getPath();
        if (URI.startsWith("/api/v1")) {
            String transactionId = null;
            MultivaluedMap<String, String> pathParameters = requestContext.getUriInfo().getPathParameters();
            if (pathParameters != null && pathParameters.get(Constants.TRANSACTION_ID_PATH_PARAM_NAME) != null) {
                transactionId = pathParameters.get(Constants.TRANSACTION_ID_PATH_PARAM_NAME).get(0);
            }
            MultivaluedMap<String, String> headers = requestContext.getHeaders();
            String method = requestContext.getMethod();
            byte[] entity = requestContext.getEntityStream().readAllBytes();
            String body = new String(entity);
            State state = Utility.getObject(body, State.class);
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<State>> violations = validator.validate(state);
            if (state != null && violations.isEmpty()) {
                if (transactionId == null) {
                    transactionId = Utility.generateTransactionId(state);
                }
                state.setTransactionId(transactionId);
                MDC.put(Constants.TRANSACTION_ID_LOG_CONFIGURATION, transactionId);
            }
            
            log.info("============== REQUEST ==============");
            if (pathParameters != null) {
                log.info("PATH PARAMS: {}", pathParameters, transactionId);
            }
            log.info("HEADERS: {}", headers, transactionId);
            log.info("METHOD: {}", method, transactionId);
            log.info("BODY: {}", state);

            logTracePropagation(transactionId, method, URI, pathParameters, headers, body);

            requestContext.setEntityStream(new ByteArrayInputStream(Utility.setTransactionIdInJson(entity, transactionId)));
            log.info("============== REQUEST ==============");

            requestContext.setProperty(Constants.TRANSACTION_ID_LOG_CONFIGURATION, transactionId);
            requestContext.setProperty(Constants.START_TIME, System.currentTimeMillis());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String URI = requestContext.getUriInfo().getPath();
        if (URI.startsWith("/api/v1")) {
            log.info("============== RESPONSE ==============");
            log.info("STATUS: {}", responseContext.getStatus());
            if (responseContext.getEntity() != null) {
                log.info("BODY: {}", Utility.getObscuredJson(responseContext.getEntity()));
            }
            log.info("============== RESPONSE ==============");
            MDC.remove(Constants.TRANSACTION_ID_LOG_CONFIGURATION);
            
            if (URI.contains("main")) {
                LatencyTracer.logElapsedTime("TaskResource.createMainScene", "Internal",requestContext.getProperty(Constants.START_TIME));
            } else if (URI.contains("next")) {
                LatencyTracer.logElapsedTime("TaskResource.createNextScene", "Internal", requestContext.getProperty(Constants.START_TIME));
            }
        }
    }

}