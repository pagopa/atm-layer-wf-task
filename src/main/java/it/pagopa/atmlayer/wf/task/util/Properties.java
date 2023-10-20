package it.pagopa.atmlayer.wf.task.util;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.Converter;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigMapping(prefix = "wf-task.config")
@RegisterForReflection
public interface Properties {

    @WithConverter(ConverterImplement.class)
    @ConfigProperty(name = "template-path")
    String templatePath();

    public static class ConverterImplement implements Converter<String> {
        @Override
        public String convert(String value) throws IllegalArgumentException, NullPointerException {
            return value.endsWith("/") ? value : value + "/";
        }

    }

}
