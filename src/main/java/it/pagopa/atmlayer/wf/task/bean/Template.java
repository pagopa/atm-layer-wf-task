package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@RegisterForReflection
@Schema(description = "Oggetto che rappresenta il template da visualizzare")
public class Template {

    @JsonProperty(access = Access.WRITE_ONLY)
    @Schema(description = "Rappresenta il Base64 della pagina HTML da visualizzare")
    private String data;

    private String type;

}
