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
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorResponse;
import it.pagopa.atmlayer.wf.task.bean.outcome.OutcomeEnum;
import it.pagopa.atmlayer.wf.task.bean.outcome.OutcomeResponse;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.ProcessingException;
import lombok.extern.slf4j.Slf4j;

@Path("/api/v1/tasks")
@Slf4j
public class TaskResource {

	@Inject
	TaskService taskService;

	@Path("/main/{functionId}")
	@POST
	@Operation(summary = "Restituisce la scena principale della funzione selezionata.", description = "CREATE della scena prinicpale con la lista dei task dato l'ID della funzione selezionata.")
	@APIResponse(responseCode = "200", description = "Operazione eseguita con successo. Il processo è terminato.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Task nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "202", description = "Operazione eseguita con successo. Il processo è in esecuzione.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "204", description = "Errore durante l'elaborazione del flusso.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "400", description = "Richiesta malformata, la descrizione può fornire dettagli sull'errore.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "500", description = "Errore generico, la descrizione può fornire dettagli sull'errore.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public RestResponse<Scene> createMainScene(
			@Parameter(description = "ID della funzione selezionata", example = "SPONTANEOUS_PAYMENT") @NotNull @PathParam("functionId") String functionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {
		try {
			Scene scene = taskService.buildFirst(functionId, state);
			if (OutcomeEnum.PROCESSING.equals(scene.getOutcome().getOutcomeEnum())) {
				return RestResponse.status(Status.ACCEPTED, scene);
			}
			if (scene.getTask() == null) {
				scene.setOutcome(new OutcomeResponse(OutcomeEnum.END));
				return RestResponse.status(Status.OK, scene);
			}
			return RestResponse.status(Status.CREATED, scene);
		} catch (ProcessingException e) {
			log.error("Unable to establish connection", e);
			throw new ErrorException(ErrorEnum.CONNECTION_PROBLEM);
		}

	}

	@Path("/next/trns/{transactionId}")
	@POST
	@Operation(summary = "Restituisce la scena successiva con la lista dei task dato l'ID del flusso.", description = "CREATE dello step successivo a quello corrente dato l'ID del flusso.")
	@APIResponse(responseCode = "200", description = "Operazione eseguita con successo. Il processo è terminato.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "201", description = "Operazione eseguita con successo. Restituisce l'oggetto Task nel body della risposta.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "202", description = "Operazione eseguita con successo. Il processo è in esecuzione.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "204", description = "Errore durante l'elaborazione a causa dell'errata progettazione della funzione.", content = @Content(schema = @Schema(implementation = Scene.class)))
	@APIResponse(responseCode = "400", description = "Richiesta malformata, la descrizione può fornire dettagli sull'errore.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@APIResponse(responseCode = "500", description = "Errore generico, la descrizione può fornire dettagli sull'errore.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public RestResponse<Scene> createNextScene(
			@Parameter(description = "ID della transazione") @NotNull @PathParam("transactionId") String transactionId,
			@Parameter(description = "Il body della richiesta con lo stato del dispositivo, delle periferiche e dei tesk eseguiti") @NotNull State state) {

		String[] transactionIdParts = transactionId.split("-");
		if (!transactionIdParts[0].equals(state.getDevice().getBankId())) {
			log.error("TransactionId not valid -> [BankId]");
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}
		if (state.getDevice().getBranchId() != null
				&& !transactionIdParts[1].equals(state.getDevice().getBranchId())) {
			log.error("TransactionId not valid -> [BranchId]");
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}
		if (state.getDevice().getCode() != null
				&& !transactionIdParts[2].equals(state.getDevice().getCode())) {
			log.error("TransactionId not valid -> [Code]");
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}
		if (state.getDevice().getTerminalId() != null
				&& !transactionIdParts[3].equals(state.getDevice().getTerminalId())) {
			log.error("TransactionId not valid -> [TerminalId]");
			throw new ErrorException(ErrorEnum.INVALID_TRANSACTION_ID);
		}
		try {
			Scene scene = taskService.buildNext(transactionId, state);
			if (OutcomeEnum.PROCESSING.equals(scene.getOutcome().getOutcomeEnum())) {
				return RestResponse.status(Status.ACCEPTED, scene);
			}
			if (scene.getTask() == null) {
				scene.setOutcome(new OutcomeResponse(OutcomeEnum.END));
				return RestResponse.status(Status.OK, scene);
			}
			return RestResponse.status(Status.CREATED, scene);
		} catch (ProcessingException e) {
			log.error("Unable to establish connection", e);
			throw new ErrorException(ErrorEnum.CONNECTION_PROBLEM);
		}

	}
}
