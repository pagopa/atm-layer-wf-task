package it.pagopa.atmlayer.wf.task.util;

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

    public static void trace(String toLog){
        message = message.concat(toLog).concat("\n");
    }

    @Scheduled(every = "2m")
    public void tracerJob(){
        if (properties.isTraceLoggingEnabled()){
            objectStoreServiceImpl.writeLog(message);
            message = new String();
        }
    }
}
