package it.pagopa.atmlayer.wf.task.client.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(Include.NON_NULL)
@ToString
@Schema(description = "Rappresenta una chiave pubblica")
@RegisterForReflection
public class PublicKey {

    @Schema(description = "Tipo di chiave", maxLength = 10, example = "RSA", format = "string")
    private String kty;

    @Schema(description = "Esponente della chiave pubblica", format = "byte", minLength = 1, maxLength = 17, example = "AQAB")
    private byte[] e;

    @Schema(description = "Uso della chiave", maxLength = 5, example = "enc", format = "string")
    private String use;

    @Schema(description = "Identificatore della chiave", maxLength = 36, format = "regex", pattern = "^[ -~]{1,2048}$")
    private String kid;
    
    @Schema(description = "Tempo di scadenza in secondi dall'epoca", example = "83647", minimum = "0", maximum = "1678975089")
    private int exp;

    @Schema(description = "Tempo di emissione in secondi dall'epoca", example = "502000", minimum = "0", maximum = "1678888689")
    private int iat;

    @Schema(description = "Modulus della chiave pubblica", format = "byte", minLength = 256, maxLength = 2048)
    private byte[] modulus;
}
