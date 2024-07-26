package it.pagopa.atmlayer.wf.task.bean;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@JsonInclude(Include.NON_NULL)
@Getter
@Schema(description = "Oggetto che rappresenta le informazioni del pan")
@RegisterForReflection
public class PanInfo {

    @ToString.Exclude
    @JsonProperty(access = Access.WRITE_ONLY)
    @Schema(required = true, description = "Pan (dato sensibile)", maxLength = 19, format = "regex", pattern = "\\b(?:\\d[ -]*?){13,19}\\b")
    @Size(max = 19, message = "Invalid pan")
    @Pattern(regexp = "\\b(?:\\d[ -]*?){13,19}\\b")
    private String pan;
    
    @Schema(description = "Circuito del pan", example = "[\"VISA\", \"MASTERCARD\"]", maxLength = 1000, format = "string")
    @Size(max = 1000, message = "Invalid circuit")
    private List<String> circuits;
    
    @Schema(description = "Nome della banca associata al pan", example = "NEXI", maxLength = 100, format = "string")
    @Size(max = 100, message = "Invalid bankName")
    private String bankName;
    
}
