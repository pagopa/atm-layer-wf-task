package it.pagopa.atmlayer.wf.task.logging.latency;

import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.logging.latency.producer.CloudWatchLogsProducer;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsAsyncClient;

@Slf4j
@RegisterForReflection
public class LatencyTracer extends CloudWatchLogsProducer {

    @Inject
    private static CloudWatchLogsAsyncClient client;

    private static StringBuilder messageBuilder = new StringBuilder();

    private static final String GROUP_NAME = "/aws/eks/fluentbit-cloudwatch/workload/pagopa/latency";

    private static final String STREAM_NAME = "latency";

    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - LOG_ID of the function to display in the log
     * @param start - the start time, when the execution is started
     */
    public static void logElapsedTime(String label, String communicationType, long start) {
        messageBuilder.append(label).append(" - Latency ").append(communicationType).append(" - Elapsed time [ms] = ")
                .append(System.currentTimeMillis() - start);
        Uni.createFrom().completionStage(
                client.putLogEvents(generatePutLogEventRequest(GROUP_NAME, STREAM_NAME, messageBuilder.toString())))
                .onFailure().invoke(throwable -> {
                    log.error("Error writing latency log on Cloudwatch: ", throwable);
                })
                .subscribe();
        messageBuilder.setLength(0);
    }

}
