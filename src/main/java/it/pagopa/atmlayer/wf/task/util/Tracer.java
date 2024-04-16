package it.pagopa.atmlayer.wf.task.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.scheduler.Scheduled;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.ConfigurationAsyncServiceImpl;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.contract.ConfigurationService;
import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@RegisterForReflection
@Slf4j
public class Tracer {

    @Inject
    private S3ObjectStoreServiceImpl objectStoreServiceImpl;

    @Inject
    private ConfigurationAsyncServiceImpl configurationAsyncServiceImpl;

    private static StringBuilder messageBuilder = new StringBuilder();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static Boolean isTraceLoggingEnabled;

    @Scheduled(every = "1h")
    public void tracerJob() {
        configurationAsyncServiceImpl.get(ConfigurationService.TRACING)
                .subscribe().with(configuration -> {
                    isTraceLoggingEnabled = configuration.isEnabled() != null ? configuration.isEnabled() : false;
                    log.info("isTraceLoggingEnabled: {}", isTraceLoggingEnabled);
                    log.info("Next tracer job starts at {}", Utility.tracerJobTimeLeft());
                    if (isTraceLoggingEnabled && messageBuilder.length() > 0) {
                        objectStoreServiceImpl.writeLog(messageBuilder.toString().replaceAll("\\{\\}", ""));
                        messageBuilder.setLength(0);
                    }
                }, throwable -> {
                    isTraceLoggingEnabled = false;
                    log.error("Error during communication with DynamoDB: {}", throwable.getMessage());
                });
    }

    public static void trace(String transactionId, String toLog) {
        if (isTraceLoggingEnabled) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            String formattedDateTime = currentDateTime.format(formatter).concat(" | ");
            messageBuilder.append(formattedDateTime).append(" ").append(transactionId).append(" | ").append(toLog)
                    .append("\n");
        }
    }

}
