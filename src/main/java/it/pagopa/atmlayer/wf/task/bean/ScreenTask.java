package it.pagopa.atmlayer.wf.task.bean;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Descrive un task di visualizzazione di una pagina HTML")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class ScreenTask extends Task {

	@Schema(required = true, description = "Nome della template HTML oppure dell'HTML embedded (CDATA ???)")
	private String template;
	
	private List<Button> buttons;

}
