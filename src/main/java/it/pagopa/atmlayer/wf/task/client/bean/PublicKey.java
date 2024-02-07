package it.pagopa.atmlayer.wf.task.client.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto che rappresenta lo stato di una transazione")
@RegisterForReflection
public class PublicKey {

    private String kty;

    private byte[] e;

    private String use;

    private String kid;
    
    private int exp;

    private int iat;

    private byte[] modulus;
}
