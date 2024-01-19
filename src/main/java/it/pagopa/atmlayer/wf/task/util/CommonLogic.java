package it.pagopa.atmlayer.wf.task.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonLogic{

    private static final String TASK_RESOURCE_CLASS_ID = "TaskResource.";
    protected static final String CREATE_MAIN_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createMainScene";
    protected static final String CREATE_NEXT_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createNextScene";


    private static final String PROCESS_REST_CLIENT_CLASS_ID = "ProcessRestClient.";
    protected static final String START_PROCESS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "startProcess";
    protected static final String NEXT_TASKS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "nextTasks";
    protected static final String RETRIEVE_VARIABLES_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "retrieveVariables";
    protected static final String GET_TOKEN_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "getToken";

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