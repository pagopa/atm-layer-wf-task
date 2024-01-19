package it.pagopa.atmlayer.wf.task.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Logging{
    
    public static final String CREATE_MAIN_SCENE_LOG_ID = "createMainScene";

    public static final String CREATE_NEXT_SCENE_LOG_ID = "createNextScene";

    public static final String START_PROCESS_LOG_ID = "startProcess";

    public static final String NEXT_TASKS_LOG_ID = "nextTasks";

    public static final String RETRIEVE_VARIABLES_LOG_ID = "retrieveVariables";

    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - the function to display in the log
     * @param start - the start time, when the execution is started
     * @param stop  - the stop time, when the execution is finished
     */
    public static void logElapsedTime(String label, long start, long stop) {
        log.info(" - {} - Elapsed time [ms] = {}", label, stop - start);
    }
}