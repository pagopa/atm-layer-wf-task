package it.pagopa.atmlayer.wf.task.bean;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Task {

	@Schema(required = true, description = "Identificativo del Task")
	private String id;

	@Schema(description = "Mappa delle variabili server --> client")
	private Map<String, String> data;

	@Schema(description = "The key/value data map on Error")
	private Map<String, String> onError;

	@Schema(description = "The timeout value")
	private int timeout;

	@Schema(description = "The key/value data map on Timeout")
	private Map<String, String> onTimeout;

	@Schema(description = "Template html")
	private String template;

	@Schema(description = "Command to execute")
	private Command command;

	@Schema(description = "Nome del template HTML della ricevuta oppure dell'HTML embedded (CDATA ???)")
	private String receiptTemplate;

	@Schema(description = "Nome della variabile in cui il Device setterà l'esito del Command")
	private String outcomeVarName;

	@Schema(description = "List of buttons")
	private List<Button> buttons;
}
