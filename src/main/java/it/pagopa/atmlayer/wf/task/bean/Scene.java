package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.pagopa.atmlayer.wf.task.bean.outcome.OutcomeResponse;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto Scene che rappresenta un Task")
@RegisterForReflection
public class Scene {

	@Schema(required = false, description = "Task e le sue proprietà", implementation = Task.class)
	private Task task;

	@Schema(required = true, description = "ID della transazione. Può essere generato dal Device alla richiesta della prima scena oppure generato dal server alla risposta della prima scena. Resta invariato fino al termine della funzione.", example = "b197bbd0-0459-4d0f-9d4a-45cdd369c018", maxLength = 36, format = "string")
	@Size(max = 36)
	private String transactionId;

	@Schema(required = true, description = "Esito dell' operazione", implementation = OutcomeResponse.class)
	private OutcomeResponse outcome;
}
