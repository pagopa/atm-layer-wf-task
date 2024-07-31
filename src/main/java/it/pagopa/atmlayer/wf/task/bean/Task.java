package it.pagopa.atmlayer.wf.task.bean;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.Schema.False;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.pagopa.atmlayer.wf.task.bean.enumartive.Command;
import it.pagopa.atmlayer.wf.task.bean.enumartive.EppMode;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@Schema(description = "Oggetto che rappresenta un task")
public class Task {

	@Schema(required = true, description = "Identificativo del Task", minLength = 0, maxLength = 36, format = "string")
	private String id;

	@Schema(description = "Mappa delle variabili generiche", additionalProperties = False.class, maxLength = 1000)
	private Map<String, Object> data;

	@Schema(description = "Mappa delle variabili da consultare in caso di errore", additionalProperties = False.class, maxLength = 1000)
	private Map<String, Object> onError;

	@Schema(description = "Valore di durata prima di andare in timeout", minimum = "0", maximum = "2147483647")
	@Positive
	private int timeout;

	@Schema(description = "Mappa delle variabili da consultare in caso di timeout", additionalProperties = False.class, maxLength = 1000)
	private Map<String, Object> onTimeout;

	@Schema(description = "Template html", implementation = Template.class)
	private Template template;

	@Schema(description = "Comando da eseguire", implementation = Command.class, enumeration = "[\"AUTHORIZE\", \"PRINT_RECEIPT\", \"SCAN_BIIL_DATA\", \"SCAN_FISCAL_CODE\", \"END\", \"GET_IBAN\", \"GET_PAN\", \"NEXT\", \"AUTHENTICATION\" ]", type = SchemaType.STRING)
	private Command command;

	@Schema(description = "Template dello scontrino", maxLength = 100000, format = "string")
	private String receiptTemplate;

	@Schema(description = "Nome della variabile in cui il Device setterà l'esito del Command", maxLength = 1000, format = "string")
	private String outcomeVarName;

	@Schema(description = "Modalità dell'epp", implementation = EppMode.class, enumeration = "[\"DATA\", \"SMS\"]", type = SchemaType.STRING)
	private EppMode eppMode;

	@Schema(description = "Lista dei bottoni", type = SchemaType.ARRAY, maxItems = 1000)
	private List<Button> buttons;
}
