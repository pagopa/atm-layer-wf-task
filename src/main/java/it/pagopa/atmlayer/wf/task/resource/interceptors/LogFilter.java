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

    Tracer tracer = new Tracer();

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
            
            log.info("============== REQUEST ==============");
            tracer.trace("============== REQUEST ==============");
            if (pathParameters != null) {
                log.info("PATH PARAMS: {}", pathParameters);
                tracer.trace("PATH PARAMS: " + pathParameters.toString());
            }
            log.info("HEADERS: {}", requestContext.getHeaders());
            tracer.trace("HEADERS: {}" + requestContext.getHeaders().toString());
            log.info("METHOD: {}", requestContext.getMethod());
            tracer.trace("HEADERS: {}" + requestContext.getMethod().toString());
            log.info("BODY: {}", state);
            if (isTraceLoggingEnabled) {
                tracer.trace("BODY: " + new String(entity));
            }

            requestContext.setEntityStream(new ByteArrayInputStream(Utility.setTransactionIdInJson(entity, transactionId)));

            log.info("============== REQUEST ==============");
            tracer.trace("============== REQUEST ==============");
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (requestContext.getUriInfo().getPath().startsWith("/api/v1")) {
            log.info("============== RESPONSE ==============");
            tracer.trace("============== RESPONSE ==============");
            log.info("STATUS: {}", responseContext.getStatus());
            tracer.trace("STATUS: {}" + responseContext.getStatus());
            if (responseContext.getEntity() != null) {
                log.info("BODY: {}", Utility.getObscuredJson(responseContext.getEntity()));
                tracer.trace("BODY: {}" + Utility.getObscuredJson(responseContext.getEntity()));
                if (isTraceLoggingEnabled) {
                    tracer.trace("BODY: " + Utility.getJson(responseContext.getEntity()));
                }
            }
            log.info("============== RESPONSE ==============");
            tracer.trace("============== RESPONSE ==============");
            MDC.remove(Constants.TRANSACTION_ID_LOG_CONFIGURATION);
        }
    }

}