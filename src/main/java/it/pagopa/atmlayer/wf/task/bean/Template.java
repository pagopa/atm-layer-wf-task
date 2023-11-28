package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.atmlayer.wf.task.bean.enumartive.TemplateType;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@RegisterForReflection
@Schema(description = "Oggetto che rappresenta il template da visualizzare")
public class Template {

    private String data;

    private TemplateType type;

}
