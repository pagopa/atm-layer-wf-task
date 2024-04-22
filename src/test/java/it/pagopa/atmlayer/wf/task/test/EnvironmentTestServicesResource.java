package it.pagopa.atmlayer.wf.task.test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.MockitoConfig;
import it.pagopa.atmlayer.wf.task.logging.latency.LatencyTracer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.File;
import java.util.Collections;
import java.util.Map;

@QuarkusTest
class EnvironmentTestServicesResource {

    @InjectMock
    @MockitoConfig(convertScopes = true)
    LatencyTracer latencyTracer;

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentTestServicesResource.class);

    public static class DockerCompose implements QuarkusTestResourceLifecycleManager {
        private DockerComposeContainer<?> dockerComposeContainer;

        @Override
        public Map<String, String> start() {

            dockerComposeContainer = new DockerComposeContainer<>(
                    new File("src/test/resources/integration-test/docker-compose.yml"))
                    .withExposedService("mockoon", 3000);

            dockerComposeContainer.withLogConsumer("mockoon", new Slf4jLogConsumer(LOGGER).withPrefix("mockoon"));
            dockerComposeContainer.start();

            return Collections.emptyMap();
        }

        @Override
        public void stop() {
            if (dockerComposeContainer != null) {
                dockerComposeContainer.stop();
            }
        }
    }

}
