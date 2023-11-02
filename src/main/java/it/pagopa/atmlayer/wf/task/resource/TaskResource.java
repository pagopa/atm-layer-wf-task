package it.pagopa.atmlayer.wf.task.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.extern.slf4j.Slf4j;

@Path("/api/v1/tasks")
@Slf4j
public class TaskResource {

	@Inject
	TaskService taskService;

	@Path("/main/{functionId}")
	@POST
	@Operation(summary = "Restituisce la scena principale della funzione selezionata", description = "CREATE della scena prinicpale con la lista dei task dato l'ID della funzione selezionata.")
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Scene nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	public RestResponse<Scene> createMainScene(
			@Parameter(description = "ID della funzione selezionata", example = "PAGAMENTO_SPONTANEO") @NotNull @PathParam("functionId") String functionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		Scene scene = taskService.buildFirst(functionId, state);

		return RestResponse.status(Status.CREATED, scene);
	}

	@Path("/next/trns/{transactionId}")
	@POST
	@Operation(summary = "Restituisce la scena successiva con la lista dei task dato l'ID del flusso.", description = "CREATE dello step successivo a quello corrente dato l'ID del flusso.")
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. restituisce l'oggetto Task nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	public RestResponse<Scene> createNextScene(
			@Parameter(description = "ID della transazione") @NotNull @PathParam("transactionId") String transactionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		if (state.getTaskId() == null || state.getTaskId().isEmpty()) {
			log.error("Task id is null or empty");
			return RestResponse.status(Status.BAD_REQUEST);
		}
		Scene scene = taskService.buildNext(transactionId, state);
		return RestResponse.status(Status.CREATED, scene);

	}
}
