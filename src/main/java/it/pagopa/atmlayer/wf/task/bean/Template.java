package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@RegisterForReflection
@Schema(description = "Oggetto che rappresenta il template da visualizzare")
public class Template {

    @JsonView(ObscureView.class)
    @Schema(description = "Rappresenta il Base64 della pagina HTML da visualizzare", maxLength = 100000, format = "string")
    private String content;

    @Schema(description = "Tipo di visualizzazione della schermata HTML", example = "FULL_SCREEN", maxLength = 11, format = "string")
    private String type;

}
