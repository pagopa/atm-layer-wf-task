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

    private static String message = new String();

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static void trace(String toLog){
        LocalDateTime currentDateTime = LocalDateTime.now();
        String formattedDateTime = currentDateTime.format(formatter).concat(" | ");
        if (message.isEmpty()){
            message = formattedDateTime.concat(" ").concat(toLog).concat("\n");
        } else {
            message = message.concat(formattedDateTime).concat(" ").concat(toLog).concat("\n");
        }
    }

    @Scheduled(every = "2m", delay = 5, delayUnit = TimeUnit.SECONDS)
    public void tracerJob(){
        if (properties.isTraceLoggingEnabled()){
            objectStoreServiceImpl.writeLog(message.replaceAll("\\{\\}", ""));
            message = new String();
        }
    }
}
