package it.pagopa.atmlayer.wf.task.bean;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Descrive un task per l'esecuzione di un comando sul Device (ATM o Chiosco)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CommandTask extends Task {

	private Command command;

	@Schema(description = "Nome del template HTML della ricevuta oppure dell'HTML embedded (CDATA ???)")
	@JsonProperty("receipt_template")
	private String receiptTemplate;
	
	@Schema(description = "Nome della variabile in cui il Device setterà l'esito del Command")
	@JsonProperty("outcome_var_name")
	private String outcomeVarName;

}
