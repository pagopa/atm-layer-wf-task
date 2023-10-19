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
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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

		log.info("RequestBody:\n{}", Utility.getJson(state));

		Scene scene = taskService.buildFirst(functionId, null, state);

		log.info("ResponseBody:\n{}", Utility.getJson(scene));

		return RestResponse.status(Status.CREATED, scene);
	}

	@Path("/main/{functionId}/trns/{trnId}")
	@POST
	@Operation(summary = "Restituisce la scena principale della funzione selezionata", description = "CREATE della scena principale con la lista dei task dato l'ID del flusso BPMN della funzione selezionata.")
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Scene nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	public RestResponse<Scene> createMainScene(
			@Parameter(description = "ID della funzione selezionata", example = "PAGAMENTO_SPONTANEO") @NotNull @PathParam("functionId") String functionId,
			@Parameter(description = "ID della transazione. Può essere generato dal Device alla richiesta della prima scena oppure generato dal server alla risposta della prima scena. Resta invariato fino al termine della funzione.", example = "b197bbd0-0459-4d0f-9d4a-45cdd369c018") @NotNull @PathParam("trnId") String transactionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		log.info("RequestBody:\n{}", Utility.getJson(state));

		Scene scene = taskService.buildFirst(functionId, transactionId, state);

		log.info("ResponseBody:\n{}", Utility.getJson(scene));

		return RestResponse.status(Status.CREATED, scene);
	}

	@Path("/next/trns/{trnId}")
	@POST
	@Operation(summary = "Restituisce la scena successiva con la lista dei task dato l'ID del flusso.", description = "CREATE dello step successivo a quello corrente dato l'ID del flusso.")
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. restituisce l'oggetto Task nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	public RestResponse<Scene> createNextScene(
			@Parameter(description = "ID della transazione") @NotNull @PathParam("trnId") String transactionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		log.info("RequestBody:\n{}", Utility.getJson(state));

		Scene scene = taskService.buildNext(transactionId, state);

		log.info("ResponseBody:\n{}", Utility.getJson(scene));

		return RestResponse.status(Status.CREATED, scene);
	}

}
