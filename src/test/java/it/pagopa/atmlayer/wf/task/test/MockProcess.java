package it.pagopa.atmlayer.wf.task.test;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.test.Mock;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;

@Mock
@RestClient
public class MockProcess implements ProcessRestClient {

    @Override
    public RestResponse<TaskResponse> startProcess(TaskRequest taskRequest) {
        TaskResponse response = new TaskResponse();
        // response.setDeviceInfo(taskRequest.getDeviceInfo());
        List<String> taskList = new ArrayList<>();
        taskList.add("gang");
        // response.setTasks(taskList);
        return RestResponse.ok(response);
    }

}
