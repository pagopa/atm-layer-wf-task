package it.pagopa.atmlayer.wf.task.logging.sensitive;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.scheduler.Scheduled;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.ConfigurationAsyncServiceImpl;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.contract.ConfigurationService;
import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for tracing and logging sensitive data.
 */
@RegisterForReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SensitiveDataTracer {

    @Inject
    private static S3ObjectStoreServiceImpl objectStoreServiceImpl;

    @Inject
    private ConfigurationAsyncServiceImpl configurationAsyncServiceImpl;

    private static StringBuilder messageBuilder = new StringBuilder();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Indicates whether trace logging is enabled.
     */
    private static Boolean isTraceLoggingEnabled = false;

    /**
     * Scheduled method to run tracer job every hour.
     */
    @Scheduled(every = "1h")
    public void tracerJob() {
        configurationAsyncServiceImpl.get(ConfigurationService.TRACING)
                .subscribe().with(configuration -> {
                    if (configuration.isEnabled() != null) {
                        isTraceLoggingEnabled = configuration.isEnabled();
                    }
                    
                    log.info("isTraceLoggingEnabled: {}", isTraceLoggingEnabled);
                    log.info("Next tracer job starts at {}", Utility.tracerJobTimeLeft());
                    if (isTraceLoggingEnabled.booleanValue() && messageBuilder.length() > 0) {
                        objectStoreServiceImpl.writeLog(messageBuilder.toString().replace("\\{\\}", ""));
                        messageBuilder.setLength(0);
                    }
                }, throwable -> {
                    isTraceLoggingEnabled = false;
                    log.error("Error during communication with DynamoDB: {}", throwable.getMessage());
                });
    }

    /**
     * Traces sensitive data.
     *
     * @param transactionId The ID of the transaction.
     * @param toLog         The data to be logged.
     */
    public static void trace(String transactionId, String toLog) {
        if (isTraceLoggingEnabled.booleanValue()) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            String formattedDateTime = currentDateTime.format(formatter).concat(" | ");
            messageBuilder.append(formattedDateTime).append(" ").append(transactionId).append(" | ").append(toLog)
                    .append("\n");
        }
    }

    public static Boolean getIsTraceLoggingEnabled() {
        return isTraceLoggingEnabled;
    }

    public static void setIsTraceLoggingEnabled(Boolean isTraceLoggingEnabled) {
        SensitiveDataTracer.isTraceLoggingEnabled = isTraceLoggingEnabled;
    }

}
