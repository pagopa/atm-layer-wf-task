package it.pagopa.atmlayer.wf.task;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(title = "Workflow Task API", description = "Esegue il workflow basato su un BPMN model", version = "1.0.0"))
@ApplicationPath("/")
public class TaskMicroservice extends Application {
}
