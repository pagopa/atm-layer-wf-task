package it.pagopa.atmlayer.wf.task.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto che rappresenta lo stato di una transazione")
public class State {

	@NotNull(message = "Device non può essere null")
	@Schema(required = true)
	private Device device;

	@Schema(description = "ID del task che da completato")
	private String taskId;

	@Schema(description = "Mappa delle variabili inviate dal Device")
	private Map<String, Object> data;
}
