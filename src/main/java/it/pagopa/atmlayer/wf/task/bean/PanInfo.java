package it.pagopa.atmlayer.wf.task.bean;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto che rappresenta le informazioni del pan")
@RegisterForReflection
public class PanInfo {

    @ToString.Exclude
    @JsonProperty(access = Access.WRITE_ONLY)
    @Schema(required = true, description = "Pan (dato sensibile)")
    private String pan;
    
    @Schema(description = "Circuito del pan", example = "[VISA, MASTERCARD]")
    private List<String> circuits;
    
    @Schema(description = "Nome della banca associata al pan", example = "NEXI")
    private String bankName;
    
}
