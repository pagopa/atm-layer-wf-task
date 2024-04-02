package it.pagopa.atmlayer.wf.task.util;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

import java.util.Optional;

@ConfigMapping(prefix = "object-store", namingStrategy = ConfigMapping.NamingStrategy.KEBAB_CASE)
@StaticInitSafe
public interface ObjectStoreProperties {

    Bucket bucket();

    Resource resource();

    interface Bucket {
        
        String name();

        Optional<String> endpointOverride();

        String region();

        Optional<String> secretKey();

        Optional<String> accessKey();
    }

    interface Resource {
        String pathTemplate();
    }

}