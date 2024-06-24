package it.pagopa.atmlayer.wf.task.logging.latency.producer;


import java.time.Instant;

import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;

/**
 * Abstract class for producing CloudWatch Logs.
 */
public abstract class CloudWatchLogsProducer {

    /**
     * Generates a PutLogEventsRequest with the provided parameters.
     *
     * @param groupName   The name of the log group.
     * @param streamName  The name of the log stream.
     * @param logMessage  The message to log.
     * @return A PutLogEventsRequest.
     */
    protected static PutLogEventsRequest generatePutLogEventRequest(String groupName, String streamName, String logMessage) {
        return PutLogEventsRequest.builder().logGroupName(groupName).logStreamName(streamName)
                .logEvents(InputLogEvent.builder().message(logMessage)
                        .timestamp(Instant.now().toEpochMilli()).build()).build(); 
    }

}