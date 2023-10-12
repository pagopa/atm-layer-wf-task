package it.pagopa.atmlayer.wf.task.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class State {
	
	@NotNull(message = "Device must not be null")
	@Schema(required = true)
	private Device device;

	@Schema(description = "ID del task che sto chiudendo (non presente nella prima chiamata)")
	@JsonProperty("task_id")
	private String taskId;

	@Schema(description = "Mappa delle variabili inviate dal Device")
	private Map<String, String> data;
}
