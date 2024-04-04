package it.pagopa.atmlayer.wf.task.util;

import java.util.concurrent.TimeUnit;

import com.oracle.svm.core.annotate.Inject;

import io.quarkus.scheduler.Scheduled;
import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;

public class Tracer {
    
    @Inject
    Properties properties;

    @Inject
    S3ObjectStoreServiceImpl objectStoreServiceImpl;

    private String message = new String();

    public void trace(String message){
        this.message.concat(message);
    }

    @Scheduled(every = "30s", delay = 5, delayUnit = TimeUnit.SECONDS)
    public void tracerJob(){
        if (properties.isTraceLoggingEnabled()){
            objectStoreServiceImpl.writeLog(message);
            message = new String();
        }
    }
}
