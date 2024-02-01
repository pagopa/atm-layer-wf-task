package it.pagopa.atmlayer.wf.task.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;

import io.opentelemetry.api.trace.Tracer;
import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorResponse;
import it.pagopa.atmlayer.wf.task.bean.outcome.OutcomeEnum;
import it.pagopa.atmlayer.wf.task.bean.outcome.OutcomeResponse;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.CommonLogic;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.ProcessingException;
import lombok.extern.slf4j.Slf4j;

@Path("/api/v1/tasks")
@Slf4j
public class TaskResource extends CommonLogic{

	@Inject
	TaskService taskService;

	@Inject
	Tracer tracer;

	@Path("/main")
	@POST
	@Operation(summary = "Restituisce la scena principale della funzione selezionata.", description = "CREATE della scena prinicpale con la lista dei task dato l'ID della funzione selezionata.")
	@APIResponse(responseCode = "200", description = "Operazione eseguita con successo. Il processo è terminato.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Task nel body della risposta.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "202", description = "Operazione eseguita con successo. Il processo è in esecuzione.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "209", description = "Errore durante l'elaborazione del flusso della funzione.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	@APIResponse(responseCode = "400", description = "Richiesta malformata, la descrizione può fornire dettagli sull'errore.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "500", description = "Errore generico, la descrizione può fornire dettagli sull'errore.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	public RestResponse<Scene> createMainScene(
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {
		
		long start = System.currentTimeMillis();

		try {
			RestResponse<Scene> response;
			Scene scene = taskService.buildFirst(Constants.FUNCTION_ID, state);
			if (OutcomeEnum.PROCESSING.equals(scene.getOutcome().getOutcomeEnum())) {
				response = RestResponse.status(Status.ACCEPTED, scene);
			} else if (scene.getTask() == null) {
				scene.setOutcome(new OutcomeResponse(OutcomeEnum.END));
				response = RestResponse.status(Status.OK, scene);
			} else {
				response = RestResponse.status(Status.CREATED, scene);
			}

			return response;
		} catch (ProcessingException e) {
			log.error("Unable to establish connection", e);
			throw new ErrorException(ErrorEnum.CONNECTION_PROBLEM);
		} finally {
			logElapsedTime(CREATE_MAIN_SCENE_LOG_ID , start);
		}

	}

	@Path("/next/trns/{transactionId}")
	@POST
	@Operation(summary = "Restituisce la scena successiva con la lista dei task dato l'ID del flusso.", description = "CREATE dello step successivo a quello corrente dato l'ID del flusso.")
	@APIResponse(responseCode = "200", description = "Operazione eseguita con successo. Il processo è terminato.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Task nel body della risposta.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "202", description = "Operazione eseguita con successo. Il processo è in esecuzione.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "209", description = "Errore durante l'elaborazione del flusso della funzione.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	@APIResponse(responseCode = "400", description = "Richiesta malformata, la descrizione può fornire dettagli sull'errore.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	@APIResponse(responseCode = "500", description = "Errore generico, la descrizione può fornire dettagli sull'errore.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
	public RestResponse<Scene> createNextScene(
			@Parameter(description = "ID della transazione") @NotNull @PathParam("transactionId") String transactionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		long start = System.currentTimeMillis();

		String[] transactionIdParts = transactionId.split("-");
		if (!transactionIdParts[0].equals(state.getDevice().getBankId())) {
			log.error("TransactionId not valid -> [BankId]");
			logElapsedTime(CREATE_NEXT_SCENE_LOG_ID, start);
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}
		if (state.getDevice().getBranchId() != null
				&& !transactionIdParts[1].equals(state.getDevice().getBranchId())) {
			log.error("TransactionId not valid -> [BranchId]");
			logElapsedTime(CREATE_NEXT_SCENE_LOG_ID, start);
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}
		if (state.getDevice().getCode() != null
				&& !transactionIdParts[2].equals(state.getDevice().getCode())) {
			log.error("TransactionId not valid -> [Code]");
			logElapsedTime(CREATE_NEXT_SCENE_LOG_ID, start);
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}
		if (state.getDevice().getTerminalId() != null
				&& !transactionIdParts[3].equals(state.getDevice().getTerminalId())) {
			log.error("TransactionId not valid -> [TerminalId]");
			logElapsedTime(CREATE_NEXT_SCENE_LOG_ID, start);
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}

		try {
			RestResponse<Scene> response = null;
			Scene scene = taskService.buildNext(transactionId, state);
			if (OutcomeEnum.PROCESSING.equals(scene.getOutcome().getOutcomeEnum())) {
				response = RestResponse.status(Status.ACCEPTED, scene);
			} else if (scene.getTask() == null) {
				scene.setOutcome(new OutcomeResponse(OutcomeEnum.END));
				response = RestResponse.status(Status.OK, scene);
				taskService.deleteToken(state);
			} else {
				response = RestResponse.status(Status.CREATED, scene);
			}

			return response;
		} catch (ProcessingException e) {
			log.error("Unable to establish connection", e);
			throw new ErrorException(ErrorEnum.CONNECTION_PROBLEM);
		} finally {
			logElapsedTime(CREATE_NEXT_SCENE_LOG_ID , start);
		}

	}
}
