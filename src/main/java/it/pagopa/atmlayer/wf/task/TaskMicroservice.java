package it.pagopa.atmlayer.wf.task;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(info = @Info(title = "Workflow Task API", description = "Esegue il workflow basato su un BPMN model", version = "1.0.0", contact = @Contact(name = "Supporto API", url = "https://www.pagopa.gov.it/", email = "info@pagopa.it")), tags = {
        @Tag(name = "Task", description = "Operazione eseguita nel workflow")
})
@ApplicationPath("/")
public class TaskMicroservice extends Application {
}
