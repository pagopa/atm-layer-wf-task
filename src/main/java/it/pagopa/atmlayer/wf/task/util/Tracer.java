package it.pagopa.atmlayer.wf.task.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.scheduler.Scheduled;
import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;
import jakarta.inject.Inject;

@RegisterForReflection
public class Tracer {
    
    @Inject
    private Properties properties;
    
    @Inject 
    private S3ObjectStoreServiceImpl objectStoreServiceImpl;

    private static StringBuilder messageBuilder = new StringBuilder();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void trace(String toLog){
        LocalDateTime currentDateTime = LocalDateTime.now();
        String formattedDateTime = currentDateTime.format(formatter).concat(" | ");
        messageBuilder.append(formattedDateTime).append(" ").append(toLog).append("\n");
    }

    @Scheduled(every = "2m", delay = 5, delayUnit = TimeUnit.SECONDS)
    public void tracerJob(){
        if (properties.isTraceLoggingEnabled() && messageBuilder.length() > 0){
            objectStoreServiceImpl.writeLog(messageBuilder.toString().replaceAll("\\{\\}", ""));
            messageBuilder.setLength(0);
        }
    }
}
