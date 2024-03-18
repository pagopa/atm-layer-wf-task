package it.pagopa.atmlayer.wf.task.bean;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Oggetto che rappresenta lo stato di una transazione")
@RegisterForReflection
public class State {

	@NotNull(message = "Device non può essere null")
	@Schema(required = true)
	private Device device;

	@Schema(description = "ID del task che da completato")
	private String taskId;

	@Schema(description = "Mappa delle variabili inviate dal Device")
	private Map<String, Object> data;

	@Schema(hidden = true)
	private String transactionId;

    @ToString.Exclude
    @JsonProperty(access = Access.WRITE_ONLY)
    @Schema(description = "Codice Fiscale dell'utente (dato sensibile)")
	private String fiscalCode;
    
    @Schema(description = "Informazioni del pan (dato sensibile)")
    private List<PanInfo> panInfo;
    
}
