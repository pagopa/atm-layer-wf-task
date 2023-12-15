package it.pagopa.atmlayer.wf.task.bean.exceptions;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder({ "errorCode", "description", "status" })
@RegisterForReflection
@Schema(description = "Oggetto che rappresenta un Errore")
public class ErrorResponse {

    @Schema(description = "Codice di errore HTTP della chiamata")
    private int status;

    @Schema(description = "Codice che identifica l'errore")
    private String errorCode;

    @Schema(description = "Descrizione dell'errore")
    private String description;

}
