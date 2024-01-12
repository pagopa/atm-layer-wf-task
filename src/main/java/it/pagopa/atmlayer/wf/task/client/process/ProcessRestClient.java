package it.pagopa.atmlayer.wf.task.client.process;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.client.process.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.process.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.process.bean.VariableResponse;
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
