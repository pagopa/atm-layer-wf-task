package it.pagopa.atmlayer.wf.task.resource.interceptors;

import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientResponseFilter;

import it.pagopa.atmlayer.wf.task.util.Properties;
import it.pagopa.atmlayer.wf.task.util.Tracer;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MilAuthFilter implements ResteasyReactiveClientRequestFilter, ResteasyReactiveClientResponseFilter{

    @Inject
    Properties properties;

    @Override
    public void filter(ResteasyReactiveClientRequestContext requestContext) {     
        if (properties.isTraceLoggingEnabled()){
            String transactionId = requestContext.getHeaderString("TransactionId");
            Tracer.trace(transactionId + " | ============== REQUEST MIL AUTH CLIENT ==============");
            Tracer.trace(transactionId + " | HEADERS: " + requestContext.getHeaders().toString());
            Tracer.trace(transactionId + " | METHOD: " + requestContext.getMethod());
            Tracer.trace(transactionId + " | ============== REQUEST MIL AUTH CLIENT ==============");
        }
    }

    @Override
    public void filter(ResteasyReactiveClientRequestContext requestContext, ClientResponseContext responseContext) {
        if (properties.isTraceLoggingEnabled()){
            String transactionId = requestContext.getHeaderString("TransactionId");
            Tracer.trace(transactionId + " | ============== RESPONSE MIL AUTH CLIENT ==============");
            Tracer.trace(transactionId + " | HEADERS: " + responseContext.getHeaders());
            if (responseContext.getEntityStream() != null) {
                Tracer.trace(transactionId + " | BODY: " + responseContext.getEntityStream().toString());
            }
            Tracer.trace(transactionId + " | ============== RESPONSE MIL AUTH CLIENT ==============");
        }
    }
    
}
