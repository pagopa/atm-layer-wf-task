package it.pagopa.atmlayer.wf.task.resource;

import org.jboss.resteasy.reactive.RestResponse;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("health/v1/task")
public class TaskResourceProbe {

	@GET
	public RestResponse<Object> health() {
		return RestResponse.ok();
	}

}
