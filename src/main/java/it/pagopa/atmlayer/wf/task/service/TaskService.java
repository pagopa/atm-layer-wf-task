package it.pagopa.atmlayer.wf.task.service;

import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;

public interface TaskService {

    /**
     * Builds the initial scene for a given function ID and state.
     *
     * @param functionId The ID of the function for which the first scene is to be built.
     * @param state      The current state of the workflow.
     * @return The initial scene for the specified function and state.
     */
    Scene buildFirst(String functionId, State state);

    /**
    * Builds the next scene in the workflow for a given transaction ID and state.
    *
    * @param transactionId The ID of the transaction for which the next scene is to be built.
    * @param state         The current state of the workflow.
    * @return The next scene for the specified transaction and state.
    */
    Scene buildNext(String transactionId, State state);


}
