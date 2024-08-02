package it.pagopa.atmlayer.wf.task.bean;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.Schema.False;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto che rappresenta lo stato di una transazione")
@RegisterForReflection
public class State {

	@NotNull(message = "Device non può essere null")
	@Schema(required = true, implementation = Device.class)
	@Valid
	private Device device;

	@Schema(description = "ID del task che da completato", maxLength = 26, format = "string")
	private String taskId;

	@Schema(description = "Mappa delle variabili inviate dal Device", additionalProperties = False.class)
	private Map<String, Object> data;

	@Schema(hidden = true, maxLength = 36, format = "string")
	private String transactionId;

  	@ToString.Exclude
  	//@JsonProperty(access = Access.WRITE_ONLY)
  	@Schema(description = "Codice Fiscale dell'utente (dato sensibile)", maxLength = 16, format = "regex", pattern = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$")
	@Size(max = 16, message = "Invalid fiscalCode")
	private String fiscalCode;
    
  	@Schema(description = "Informazioni del pan (dato sensibile)", type = SchemaType.ARRAY, maxItems = 100)
  	@Valid
  	private List<PanInfo> panInfo;
    
}
