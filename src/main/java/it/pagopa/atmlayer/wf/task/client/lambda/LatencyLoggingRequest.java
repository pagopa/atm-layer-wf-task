package it.pagopa.atmlayer.wf.task.client.lambda;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;


@RegisterForReflection
@Data
@Builder
public class LatencyLoggingRequest {

	@JsonProperty("latency_type")
	private String latencyType;
	
	@JsonProperty("latency_value")
	private String latencyValue;

}
