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

    @Schema(description = "Codice di errore HTTP della chiamata", minimum = "200", maximum = "500")
    private int status;

    @Schema(description = "Codice che identifica l'errore", example = "GENERIC_ERROR", format = "String", maxLength = 24)
    private String errorCode;

    @Schema(description = "Descrizione dell'errore", example = "Could not connect to other microservices", format = "String", maxLength = 40)
    private String description;

}
