package it.pagopa.atmlayer.wf.task.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Logging{

    public static final String TASK_RESOURCE_CLASS_ID = "TaskResource.";
    public static final String CREATE_MAIN_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createMainScene";
    public static final String CREATE_NEXT_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createNextScene";


    public static final String PROCESS_REST_CLIENT_CLASS_ID = "ProcessRestClient.";
    public static final String START_PROCESS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "startProcess";
    public static final String NEXT_TASKS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "nextTasks";
    public static final String RETRIEVE_VARIABLES_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "retrieveVariables";
    public static final String GET_TOKEN_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "getToken";

    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - LOG_ID of the function to display in the log [Pattern -> CLASS.FUNCTION]
     * @param start - the start time, when the execution is started
     * @param stop  - the stop time, when the execution is finished
     */
    public static void logElapsedTime(String label, long start) {
        long stop = System.currentTimeMillis();
        log.info(" - {} - Elapsed time [ms] = {}", label, stop - start);
    }
}