package it.pagopa.atmlayer.wf.task.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.Schema.False;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Oggetto bottone")
public class Button {

	@Schema(description = "Id del bottone", format = "String", maxLength = 1000)
	@Size(max = 1000)
	private String id;

	@Schema(description = "Mappa delle variabili del bottone", additionalProperties = False.class)
	private Map<String, Object> data;
}
