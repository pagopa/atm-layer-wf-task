package it.pagopa.atmlayer.wf.task.logging.latency.producer;


import java.time.Instant;

import software.amazon.awssdk.services.cloudwatchlogs.model.InputLogEvent;
import software.amazon.awssdk.services.cloudwatchlogs.model.PutLogEventsRequest;

public abstract class CloudWatchLogsProducer {

    protected static PutLogEventsRequest generatePutLogEventRequest(String groupName, String streamName, String logMessage) {
        return PutLogEventsRequest.builder().logGroupName(groupName).logStreamName(streamName)
                .logEvents(InputLogEvent.builder().message(logMessage)
                        .timestamp(Instant.now().toEpochMilli()).build()).build(); 
    }

}