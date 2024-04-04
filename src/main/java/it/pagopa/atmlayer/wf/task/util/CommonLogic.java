package it.pagopa.atmlayer.wf.task.util;

import it.pagopa.atmlayer.wf.task.service.impl.S3ObjectStoreServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class CommonLogic{

    @Inject
    protected Properties properties;

    private static final String TASK_RESOURCE_CLASS_ID = "TaskResource.";
    protected static final String CREATE_MAIN_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createMainScene";
    protected static final String CREATE_NEXT_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createNextScene";


    private static final String PROCESS_REST_CLIENT_CLASS_ID = "ProcessRestClient.";
    protected static final String START_PROCESS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "startProcess";
    protected static final String NEXT_TASKS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "nextTasks";
    protected static final String RETRIEVE_VARIABLES_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "retrieveVariables";
    private static final String MIL_AUTH_REST_CLIENT_CLASS_ID = "MilAuthRestClient.";
    protected static final String GET_TOKEN_LOG_ID = MIL_AUTH_REST_CLIENT_CLASS_ID + "getToken";
    protected static final String DELETE_TOKEN_LOG_ID = MIL_AUTH_REST_CLIENT_CLASS_ID + "deleteToken";

    protected boolean isTraceLoggingEnabled;

    @Inject
    protected S3ObjectStoreServiceImpl objectStoreServiceImpl;

    @PostConstruct
    public void init() {
        isTraceLoggingEnabled = properties.isTraceLoggingEnabled();
    }

    /**
     * This method serves as a provider of an <b>auxiliary logger</b> for tracing purpose.
     * If trace logging is enabled in properties the string passed to the method will
     * be logged also in the wf-task-trace.log file. 
     * 
     * @param string - string to log
     * @see application.properties
     */
    protected void logTracePropagation(String string){
        log.info(string);
        if (isTraceLoggingEnabled) {
            objectStoreServiceImpl.writeLog(string);
        }
    }

    /**
     * This method serves as a provider of an <b>auxiliary logger</b> for tracing purpose.
     * If trace logging is enabled in properties the string passed to the method will
     * be logged also in the wf-task-trace.log file. 
     * 
     * @param string - string to log
     * @param object - object to log
     * @see application.properties
     */
    protected void logTracePropagation(String string, Object object){
        log.info(string, object);
        if (isTraceLoggingEnabled) {
            objectStoreServiceImpl.writeLog(string + object.toString());
        }
    }

    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - LOG_ID of the function to display in the log
     * @param start - the start time, when the execution is started
     * @param stop  - the stop time, when the execution is finished
     */
    protected static void logElapsedTime(String label, long start) {
        long stop = System.currentTimeMillis();
        log.info(" {} - Elapsed time [ms] = {}", label, stop - start);
    }
}