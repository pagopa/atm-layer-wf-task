package it.pagopa.atmlayer.wf.task.util;

import java.util.concurrent.TimeUnit;


import io.quarkus.scheduler.Scheduled;
import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;
import jakarta.inject.Inject;

public class Tracer {
    
    @Inject
    Properties properties;

    @Inject
    S3ObjectStoreServiceImpl objectStoreServiceImpl;

    private static String message = new String();

    public static void trace(String msg){
        message.concat(msg);
    }

    @Scheduled(every = "30s", delay = 5, delayUnit = TimeUnit.SECONDS)
    public void tracerJob(){
        if (properties.isTraceLoggingEnabled()){
            objectStoreServiceImpl.writeLog(message);
            message = new String();
        }
    }
}
