package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.pagopa.atmlayer.wf.task.bean.enumartive.PeripheralStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto che rappresenta una periferica del Device e il suo stato")
public class Peripheral {

	@Schema(required = true, description = "Label che identifica una specifica periferica", example = "PRINTER")
	private String id;

	@Schema(description = "Nome testuale della periferica", example = "Receipt printer")
	private String name;

	@Schema(required = true, description = "Stato della periferica")
	private PeripheralStatus status;
}
