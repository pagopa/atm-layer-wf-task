package it.pagopa.atmlayer.wf.task.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@RegisterRestClient
@Path("/api/v1/processes")
public interface ProcessRestClient {

    @POST
    RestResponse<TaskResponse> startProcess(TaskRequest taskRequest);

}
