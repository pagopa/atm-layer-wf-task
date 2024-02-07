package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
    @Schema(description = "Pan (dato sensibile)")
    private String pan;
    
    @Schema(description = "Circuito del pan")
    private String circuit;
    
    @Schema(description = "Nome della banca associata al pan")
    private String bankName;
    
}
