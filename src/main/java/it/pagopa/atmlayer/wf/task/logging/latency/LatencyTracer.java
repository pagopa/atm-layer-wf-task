package it.pagopa.atmlayer.wf.task.logging.latency;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.logging.latency.producer.CloudWatchLogsProducer;
import it.pagopa.atmlayer.wf.task.util.Properties;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsAsyncClient;

/**
 * Class for logging latency data.
 */
@Slf4j
@Singleton
public class LatencyTracer extends CloudWatchLogsProducer {

    @Inject
    private Properties properties;
    
    @Inject
    private CloudWatchLogsAsyncClient client;

    private static StringBuilder messageBuilder = new StringBuilder();

    /**
     * Logs the elapsed time occurred for the processing.
     *
     * @param label            LOG_ID of the function to display in the log
     * @param communicationType The type of communication
     * @param start            The start time, when the execution is started
     */
    public void logElapsedTime(String label, String communicationType, Object start) {
        messageBuilder.append(label).append(" - Latency ").append(communicationType).append(" - Elapsed time [ms] = ")
                .append(System.currentTimeMillis() - ((Number) start).longValue());
        Uni.createFrom().completionStage(
                client.putLogEvents(generatePutLogEventRequest(properties.cloudwatch().groupName(), properties.cloudwatch().streamName(), messageBuilder.toString())))
                .onFailure().invoke(throwable -> {
                    log.error("Error writing latency log on Cloudwatch: ", throwable);
                })
                .subscribe();
        messageBuilder.setLength(0);
    }

}
