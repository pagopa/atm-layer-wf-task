package it.pagopa.atmlayer.wf.task.test;

import java.util.ArrayList;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.test.Mock;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;

@Mock
@RestClient
public class MockProcess implements ProcessRestClient {

    @Override
    public RestResponse<TaskResponse> startProcess(TaskRequest taskRequest) {
        TaskResponse taskResponse = new TaskResponse();
        Task task = new Task();
        task.setId("id1");
        task.setPriority(1);
        taskResponse.setTasks(new ArrayList<Task>());
        taskResponse.getTasks().add(task);
        taskResponse.setTransactionId("trnId1");
        return RestResponse.ok(taskResponse);
    }

    public RestResponse<VariableResponse> retrieveVariables(VariableRequest taskRequest) {
        return null;
    }

    public RestResponse<TaskResponse> nextTasks(TaskRequest taskRequest) {
        return null;
    }

}
