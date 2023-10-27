package it.pagopa.atmlayer.wf.task.util;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.config.ConfigMapping;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigMapping(prefix = "wf-task.config")
@RegisterForReflection
public interface Properties {

    @ConfigProperty(name = "html-charset")
    String htmlCharset();

}
