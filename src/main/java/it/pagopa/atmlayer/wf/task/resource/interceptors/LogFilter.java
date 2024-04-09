package it.pagopa.atmlayer.wf.task.resource.interceptors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;

import org.slf4j.MDC;

import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.util.CommonLogic;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Tracer;
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
        
        if (requestContext.getUriInfo().getPath().startsWith("/api/v1")) {
            String transactionId = null;
            MultivaluedMap<String, String> pathParameters = requestContext.getUriInfo().getPathParameters();
            if (pathParameters != null && pathParameters.get(Constants.TRANSACTION_ID_PATH_PARAM_NAME) != null) {
                transactionId = pathParameters.get(Constants.TRANSACTION_ID_PATH_PARAM_NAME).get(0);
            }
            byte[] entity = requestContext.getEntityStream().readAllBytes();
            State state = Utility.getObject(new String(entity), State.class);
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

            requestContext.setProperty("transactionId", transactionId);
            
            logTracePropagation("============== REQUEST ==============", transactionId);
            if (pathParameters != null) {
                logTracePropagation("PATH PARAMS: {}", pathParameters, transactionId);
            }
            logTracePropagation("HEADERS: {}", requestContext.getHeaders(), transactionId);
            logTracePropagation("METHOD: {}", requestContext.getMethod(), transactionId);

            log.info("BODY: {}", state);
            if (isTraceLoggingEnabled) {
                Tracer.trace(transactionId + " BODY: ".concat(new String(entity)));
            }

            requestContext.setEntityStream(new ByteArrayInputStream(Utility.setTransactionIdInJson(entity, transactionId)));
            logTracePropagation("============== REQUEST ==============", transactionId);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (requestContext.getUriInfo().getPath().startsWith("/api/v1")) {
            String transactionId = String.valueOf(requestContext.getProperty("transactionId"));

            logTracePropagation("============== RESPONSE ==============", transactionId);
            logTracePropagation("STATUS: {}", responseContext.getStatus(), transactionId);
            if (responseContext.getEntity() != null) {
                log.info("BODY: {}", Utility.getObscuredJson(responseContext.getEntity()));
                if (isTraceLoggingEnabled) {
                    Tracer.trace((transactionId + " BODY: " + Utility.getJson(responseContext.getEntity())));
                }
            }
            logTracePropagation("============== RESPONSE ==============", transactionId);
            MDC.remove(Constants.TRANSACTION_ID_LOG_CONFIGURATION);
        }
    }

}