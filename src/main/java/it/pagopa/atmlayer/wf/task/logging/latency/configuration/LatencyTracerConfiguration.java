package it.pagopa.atmlayer.wf.task.logging.latency.configuration;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.UnlessBuildProfile;
import it.pagopa.atmlayer.wf.task.logging.latency.LatencyTracer;
import it.pagopa.atmlayer.wf.task.logging.latency.NoopTracer;
import it.pagopa.atmlayer.wf.task.logging.latency.Tracer;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@Dependent
public class LatencyTracerConfiguration {
    
    @Produces
    @UnlessBuildProfile(anyOf = "dev, test, prod") 
    public Tracer realTracer() {
        return new LatencyTracer();
    }

    @Produces
    @DefaultBean
    public Tracer noopTracer() {
        return new NoopTracer();
    }
}
