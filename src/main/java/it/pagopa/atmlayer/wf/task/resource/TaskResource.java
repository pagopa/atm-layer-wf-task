package it.pagopa.atmlayer.wf.task.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1/tasks")
public class TaskResource {

	@Inject
	TaskService taskService;

	private static final Logger LOG = Logger.getLogger(TaskResource.class);

	@Path("/main/{functionId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Restituisce la scena principale della funzione selezionata", description = "CREATE della scena prinicpale con la lista dei task dato l'ID della funzione selezionata.")
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Scene nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	public Response createMainScene(
			@Parameter(description = "ID della funzione selezionata", example = "PAGAMENTO_SPONTANEO") @NotNull @PathParam("functionId") String functionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		// System.out.println(requestBody);
		logRequest(state);

		Scene scene = taskService.buildMain(functionId, null, state);

		logResponse(scene);

		return Response.status(201).entity(scene).build();
	}

	@Path("/main/{functionId}/trns/{trnId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Restituisce la scena principale della funzione selezionata", description = "CREATE della scena principale con la lista dei task dato l'ID del flusso BPMN della funzione selezionata.")
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Scene nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	public Response createMainScene(
			@Parameter(description = "ID della funzione selezionata", example = "PAGAMENTO_SPONTANEO") @NotNull @PathParam("functionId") String functionId,
			@Parameter(description = "ID della transazione. Può essere generato dal Device alla richiesta della prima scena oppure generato dal server alla risposta della prima scena. Resta invariato fino al termine della funzione.", example = "b197bbd0-0459-4d0f-9d4a-45cdd369c018") @NotNull @PathParam("trnId") String transactionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		// System.out.println(requestBody);
		logRequest(state);

		Scene scene = taskService.buildMain(functionId, transactionId, state);

		logResponse(scene);

		return Response.status(201).entity(taskService.buildMain(functionId, transactionId, state)).build();
	}

	@Path("/next/trns/{trnId}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Restituisce la scena successiva con la lista dei task dato l'ID del flusso.", description = "CREATE dello step successivo a quello corrente dato l'ID del flusso.")
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. restituisce l'oggetto Task nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	public Response createNextScene(
			@Parameter(description = "ID della transazione") @NotNull @PathParam("trnId") String transactionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		logRequest(state);

		Scene scene = taskService.buildNext(transactionId);

		logResponse(scene);

		return Response.ok().entity(taskService.buildNext(transactionId)).build();
	}

	private void logRequest(Object object) {
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.WRAP_ROOT_VALUE);
		try {
			LOG.info("\nRequestBody:\n" + om.writerWithDefaultPrettyPrinter().writeValueAsString(object));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void logResponse(Object object) {
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.WRAP_ROOT_VALUE);
		try {
			LOG.info("\nResponseBody:\n" + om.writerWithDefaultPrettyPrinter().writeValueAsString(object));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
