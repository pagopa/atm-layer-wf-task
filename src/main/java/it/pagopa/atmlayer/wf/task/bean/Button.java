package it.pagopa.atmlayer.wf.task.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Oggetto bottone")
public class Button {

	@Schema(description = "Id del bottone")
	private String id;

	@Schema(description = "Mappa delle variabili del bottone")
	private Map<String, Object> data;
}
