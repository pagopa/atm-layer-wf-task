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

    @JsonView(ObscureLog.class)
    @Schema(description = "Rappresenta il Base64 della pagina HTML da visualizzare")
    private String content;

    @Schema(description = "Tipo di visualizzazione della schermata HTML")
    private String type;

    public static class ObscureLog {
    }

}
