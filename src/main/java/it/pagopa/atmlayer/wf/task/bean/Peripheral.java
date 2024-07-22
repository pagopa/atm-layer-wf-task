package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.pagopa.atmlayer.wf.task.bean.enumartive.PeripheralStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto che rappresenta una periferica del Device e il suo stato")
public class Peripheral {

	@Schema(required = true, description = "Label che identifica una specifica periferica", example = "PRINTER", maxLength = 1000, format = "string")
	@Size(max = 1000, message = "Invalid peripheral id")
	private String id;

	@Schema(description = "Nome testuale della periferica", example = "Receipt printer", maxLength = 1000, format = "string")
	private String name;

	@Schema(required = true, description = "Stato della periferica", implementation = PeripheralStatus.class, enumeration = "[\"OK\", \"WARNING\", \"KO\"]", type = SchemaType.STRING)
	private PeripheralStatus status;
}
