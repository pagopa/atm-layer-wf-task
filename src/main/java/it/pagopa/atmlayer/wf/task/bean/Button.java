package it.pagopa.atmlayer.wf.task.bean;

import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "The button object.")
public class Button {

	@Schema(description = "The button id.")
	private String id;

	private Map<String, String> data;
}
