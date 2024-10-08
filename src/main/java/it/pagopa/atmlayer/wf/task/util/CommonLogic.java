package it.pagopa.atmlayer.wf.task.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.PanInfo;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.client.bean.PublicKey;
import it.pagopa.atmlayer.wf.task.client.bean.Token;
import it.pagopa.atmlayer.wf.task.logging.sensitive.SensitiveDataTracer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CommonLogic {

    private static final String TASK_RESOURCE_CLASS_ID = "TaskResource.";
    protected static final String CREATE_MAIN_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createMainScene";
    protected static final String CREATE_NEXT_SCENE_LOG_ID = TASK_RESOURCE_CLASS_ID + "createNextScene";

    private static final String PROCESS_REST_CLIENT_CLASS_ID = "ProcessRestClient.";
    protected static final String START_PROCESS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "startProcess";
    protected static final String NEXT_TASKS_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "nextTasks";
    protected static final String RETRIEVE_VARIABLES_LOG_ID = PROCESS_REST_CLIENT_CLASS_ID + "retrieveVariables";

    private static final String MIL_AUTH_REST_CLIENT_CLASS_ID = "MilAuthRestClient.";
    protected static final String GET_TOKEN_LOG_ID = MIL_AUTH_REST_CLIENT_CLASS_ID + "getToken";
    protected static final String DELETE_TOKEN_LOG_ID = MIL_AUTH_REST_CLIENT_CLASS_ID + "deleteToken";

    private static final String TOKENIZATION_REST_CLIENT_CLASS_ID = "TokenizationRestClient.";
    protected static final String GET_TOKEN_TOKENIZER_LOG_ID = TOKENIZATION_REST_CLIENT_CLASS_ID + "getToken";

    @Inject
    protected Properties properties;

    /**
     * This method serves as a provider of an <b>auxiliary logger</b> for tracing
     * purpose.
     * If trace logging is enabled in properties the string passed to the method
     * will
     * be logged also in the wf-task-trace.log file.
     * 
     * @param string - string to log
     * @see application.properties
     */
    protected void logTracePropagation(String transactionId, String method, String uri,
            MultivaluedMap<String, String> pathParameters, MultivaluedMap<String, String> headers, String body) {
            StringBuilder messageBuilder = new StringBuilder(" REQUEST ")
                    .append(method)
                    .append(" URI: ")
                    .append(uri);

            if (!Objects.isNull(pathParameters) && !pathParameters.isEmpty()) {
                messageBuilder.append(" - PATH PARAMS: ").append(pathParameters.toString());
            }

            if (!Objects.isNull(headers) && !headers.isEmpty()) {
                messageBuilder.append(" - HEADERS: ").append(headers.toString());
            }

            if (!Objects.isNull(body) && !body.isEmpty()) {
                messageBuilder.append(" - BODY: ").append(body);
            }

            SensitiveDataTracer.trace(transactionId, messageBuilder.toString());
    }

    protected void traceMilAuthClientComm(State state, Device device,
            RestResponse<Token> restTokenResponse) {
            String milAuthClientAddress = System.getenv("MIL_AUTH_SERVICE_ADDRESS");
            String transactionId = state.getTransactionId();
            StringBuilder requestMessageBuilder = new StringBuilder(" REQUEST POST URI: ")
                    .append(milAuthClientAddress).append("/token - HEADERS: ")
                    .append("{AcquirerId: ").append(Objects.toString(device.getBankId()))
                    .append(" Channel: ").append(device.getChannel().name())
                    .append(" FiscalCode: ").append(Objects.toString(state.getFiscalCode()))
                    .append(" TerminalId: ").append(Objects.toString(device.getTerminalId()))
                    .append(" TransactionId: ").append(transactionId).append("}");

            SensitiveDataTracer.trace(transactionId, requestMessageBuilder.toString());

            if (restTokenResponse != null) {
                StringBuilder responseMessageBuilder = new StringBuilder(" RESPONSE POST URI: ")
                        .append(milAuthClientAddress).append("/token - STATUS: ")
                        .append(restTokenResponse.getStatus());
                if (restTokenResponse.getEntity() != null) {
                    responseMessageBuilder.append(" - BODY: Access token: ")
                            .append(restTokenResponse.getEntity().getAccessToken());
                }
                SensitiveDataTracer.trace(transactionId, responseMessageBuilder.toString());
            } else {
                SensitiveDataTracer.trace(transactionId, " - Error while communicating with MilAuthenticator. . .");
            }
    }

    protected void tracePanInfoAndKey(String transactionId, RestResponse<PublicKey> restPanTokenizationKeyResponse, List<PanInfo> panInfoList) {
            String tokenizetionClientAddress = System.getenv("TOKENIZATION_ADDRESS");
            if (restPanTokenizationKeyResponse != null) {
                StringBuilder responseMessageBuilder = new StringBuilder(" RESPONSE GET URI: ")
                        .append(tokenizetionClientAddress).append("/key - STATUS: ")
                        .append(restPanTokenizationKeyResponse.getStatus());
                if (restPanTokenizationKeyResponse.getEntity() != null) {
                    responseMessageBuilder.append(" - BODY: ").append(restPanTokenizationKeyResponse.getEntity().toString());
                }
                SensitiveDataTracer.trace(transactionId, responseMessageBuilder.toString());
            }

            if (!Objects.isNull(panInfoList) && !panInfoList.isEmpty()) {
                StringBuilder panMessageBuilder = new StringBuilder(" RETRIEVED PAN LIST: ").append(panInfoList.stream().map(PanInfo::getPan).collect(Collectors.joining(", ")));
                SensitiveDataTracer.trace(transactionId, panMessageBuilder.toString());
            }
    }

    /**
     * Logs the elapsed time occurred for the processing.
     * 
     * @param label - LOG_ID of the function to display in the log
     * @param start - the start time, when the execution is started
     * @param stop  - the stop time, when the execution is finished
     */
    protected static void logElapsedTime(String label, long start) {
        long stop = System.currentTimeMillis();
        log.info(" {} - Elapsed time [ms] = {}", label, stop - start);
    }

}