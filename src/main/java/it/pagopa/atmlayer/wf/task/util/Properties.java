package it.pagopa.atmlayer.wf.task.util;

import java.util.Map;

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

    @ConfigProperty(name = "html-charset")
    String htmlCharset();

    @WithConverter(ConverterImplement.class)
    @ConfigProperty(name = "cdn-url")
    String cdnUrl();

    @WithConverter(ConverterImplement.class)
    @ConfigProperty(name = "html-resources-path")
    String htmlResourcesPath();
    
    @ConfigProperty(name = "tokenization-is-mock")
    boolean tokenizationIsMock();
    
    @ConfigProperty(name = "escape")
    Map<String, String> escape();

    Bucket bucket();

    Resource resource();

    interface Bucket {
        @ConfigProperty(name = "name")
        String name();

        @ConfigProperty(name = "region")
        String region();

    }

    interface Resource {
        @ConfigProperty(name = "pathTemplate")
        String pathTemplate();
    }

    public static class ConverterImplement implements Converter<String> {
        @Override
        public String convert(String value) throws IllegalArgumentException, NullPointerException {
            return value.endsWith("/") ? value : value + "/";
        }
    }
}
