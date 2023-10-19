package it.pagopa.atmlayer.wf.task.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "process-rest-client")
public interface ProcessRestClient {

    @POST
    @Path("/start")
    RestResponse<TaskResponse> startProcess(TaskRequest taskRequest);

    @POST
    @Path("/next")
    RestResponse<TaskResponse> nextTasks(TaskRequest taskRequest);

    @POST
    @Path("/variables")
    RestResponse<VariableResponse> retrieveVariables(VariableRequest taskRequest);

}
