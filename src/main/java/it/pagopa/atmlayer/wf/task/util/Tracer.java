package it.pagopa.atmlayer.wf.task.util;

import java.util.concurrent.TimeUnit;


import io.quarkus.scheduler.Scheduled;
import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Tracer {
    
    @Inject
    Properties properties;

    @Inject
    S3ObjectStoreServiceImpl objectStoreServiceImpl;

    private static String message = new String();

    public static void trace(String msg){
        log.info("msg: " + msg);
        message.concat(msg);
        log.info("message: " + message);
    }

    @Scheduled(every = "30s", delay = 5, delayUnit = TimeUnit.SECONDS)
    public void tracerJob(){
        if (properties.isTraceLoggingEnabled()){
            log.info("writing: " + message);
            objectStoreServiceImpl.writeLog(message);
            message = new String();
        }
    }
}
