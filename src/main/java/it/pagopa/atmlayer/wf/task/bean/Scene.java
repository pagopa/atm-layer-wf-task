package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@Schema(description = "L'oggetto Scene che rappresenta un Task di tipo ScreenTask o CommandTask")
public class Scene {

	@JsonProperty("task")
	private Task task;

	@Schema(required = true, description = "ID della transazione. Può essere generato dal Device alla richiesta della prima scena oppure generato dal server alla risposta della prima scena. Resta invariato fino al termine della funzione.", example = "b197bbd0-0459-4d0f-9d4a-45cdd369c018")
	@JsonProperty("trn_id")
	private String transactionId;

}
