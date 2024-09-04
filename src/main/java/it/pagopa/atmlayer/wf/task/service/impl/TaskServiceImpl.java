package it.pagopa.atmlayer.wf.task.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.parser.Parser;
import org.slf4j.MDC;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import it.pagopa.atmlayer.wf.task.bean.Button;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.PanInfo;
import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.Template;
import it.pagopa.atmlayer.wf.task.bean.enumartive.Command;
import it.pagopa.atmlayer.wf.task.bean.enumartive.EppMode;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorEnum;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.bean.outcome.OutcomeEnum;
import it.pagopa.atmlayer.wf.task.bean.outcome.OutcomeResponse;
import it.pagopa.atmlayer.wf.task.client.MilAuthClient;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.TokenizationRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.AuthParameters;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.GetTokenRequest;
import it.pagopa.atmlayer.wf.task.client.bean.GetTokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.PanInformation;
import it.pagopa.atmlayer.wf.task.client.bean.PublicKey;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.Token;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import it.pagopa.atmlayer.wf.task.service.TokenService;
import it.pagopa.atmlayer.wf.task.util.CommonLogic;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class TaskServiceImpl extends CommonLogic implements TaskService {

    @RestClient
    ProcessRestClient processRestClient;

    @RestClient
    MilAuthClient milAuthClient;

    @RestClient
    TokenizationRestClient tokenizationClient;
    
    @Inject
    TokenService tokenService;
    
    /*
     * Variabile che indica se si sta eseguendo una comunicazione esterna
     */
    Boolean externalComm = false;

    public Boolean getExternalComm() {
		return externalComm;
	}

	@Override
    public Scene buildFirst(String functionId, State state) {
        /*
         * new Thread(() -> {
         * getToken(state);
         * }).start();
         */
        Map<String, Object> data = state.getData();
        if (data == null) {
            state.setData(new HashMap<String, Object>());
            data = state.getData();
        }

        Scene scene = buildSceneStart(functionId, state.getTransactionId(), state);
        scene.setTransactionId(state.getTransactionId());
        return scene;
    }

    @Override
	public Scene buildNext(String transactionId, State state) {
    	RestResponse<Token> tokenComm = null;
		if (!Objects.isNull(state.getFiscalCode()) && !state.getFiscalCode().isEmpty()) {
			log.debug("Fiscal code found!");
			String token = null;
			try {
				tokenComm = tokenService.generateToken(AuthParameters.builder().terminalId(!Objects.isNull(state.getDevice().getTerminalId()) ? state.getDevice().getTerminalId() : state.getDevice().getBankId() + state.getDevice().getCode())
						.transactionId(transactionId).acquirerId(state.getDevice().getBankId()).channel(state.getDevice().getChannel().name()).fiscalCode(state.getFiscalCode()).build());
	        } catch (WebApplicationException e) {
	        	log.warn("Calling milAuth Status: [{}]", e.getResponse().getStatus());
	        } finally {
	        	externalComm = true;
	        }
			
			if (tokenComm != null && tokenComm.getStatus() == 200) {
                token = tokenComm.getEntity().getAccessToken();
                log.info("Retrieved token: [{}]", token);
            }
			
			state.getData().put("millAccessToken", token);
			traceMilAuthClientComm(state, state.getDevice(), tokenComm);
		}

		Scene scene = buildSceneNext(transactionId, state);
		scene.setTransactionId(transactionId);
		return scene;
	}

    private Scene buildSceneStart(String functionId, String transactionId, State state) {
        TaskRequest taskRequest = buildTaskRequest(state, transactionId, functionId);
        RestResponse<TaskResponse> restTaskResponse = null;
        long start = System.currentTimeMillis();

        try {
            log.info("Calling start process: [{}]", taskRequest);
            restTaskResponse = processRestClient.startProcess(taskRequest);
        } catch (WebApplicationException e) {
            log.error("Error calling process service", e);
            if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                throw new ErrorException(ErrorEnum.GET_TASKS_ERROR);
            }
            throw new ErrorException(ErrorEnum.PROCESS_ERROR);
        } finally {
            logElapsedTime(START_PROCESS_LOG_ID, start);
        }

        return manageTaskResponse(restTaskResponse);
    }

    private Scene buildSceneNext(String transactionId, State state) {
        TaskRequest taskRequest = buildTaskRequest(state, transactionId, null);
        RestResponse<TaskResponse> restTaskResponse = null;
        long start = System.currentTimeMillis();
        
        taskRequest.getVariables().put(Constants.EXTERNAL_COMM, false);

        
        try {
            log.info("Calling next task: [{}]", taskRequest);
            restTaskResponse = processRestClient.nextTasks(taskRequest);
        } catch (WebApplicationException e) {
            log.error("Error calling process service", e);
            if (e.getResponse().getStatus() == StatusCode.INTERNAL_SERVER_ERROR) {
                throw new ErrorException(ErrorEnum.GET_TASKS_ERROR);
            }
            throw new ErrorException(ErrorEnum.PROCESS_ERROR);
        } finally {
            logElapsedTime(NEXT_TASKS_LOG_ID, start);
        }

        return manageTaskResponse(restTaskResponse);
    }

    private Scene manageTaskResponse(RestResponse<TaskResponse> restTaskResponse) {
        Scene scene = new Scene();
        if (restTaskResponse.getStatus() == 200) {
            log.info("Retrieved process: [{}]", restTaskResponse.getEntity());
            scene.setOutcome(new OutcomeResponse(OutcomeEnum.OK));
            scene.setTask(manageOkResponse(restTaskResponse.getEntity()));
            return scene;
        } else if (restTaskResponse.getStatus() == 202) {
        	externalComm = true;
            log.info("Retrieved process: [{}]", restTaskResponse.getEntity());
            scene.setOutcome(new OutcomeResponse(OutcomeEnum.PROCESSING));
            return scene;
        } else {
            throw new ErrorException(ErrorEnum.GET_TASKS_ERROR);
        }
    }

    /**
     * Gestisce la risposta OK recuperando il primo task ordinato per priorità, 
     * chiamando il servizio di recupero variabili e processando la risposta.
     *
     * @param response la risposta del task contenente la lista dei task
     * @return il task ATM elaborato
     * @throws ErrorException se si verifica un errore nel recupero delle variabili o nel processo
     */
    private it.pagopa.atmlayer.wf.task.bean.Task manageOkResponse(TaskResponse response) {
        it.pagopa.atmlayer.wf.task.bean.Task atmTask = null;

        Optional<Task> optionalTask = response.getTasks().stream()
                                              .min(Comparator.comparingInt(Task::getPriority));

        if (optionalTask.isPresent()) {
            Task workingTask = optionalTask.get();
            atmTask = new it.pagopa.atmlayer.wf.task.bean.Task();
            VariableRequest variableRequest = createVariableRequestForTemplate(workingTask, atmTask);
            log.info("Calling retrieve variables for task id: [{}]", workingTask.getId());
            
            if (!Objects.isNull(variableRequest.getVariables())){
            	variableRequest.getVariables().add(Constants.EXTERNAL_COMM);
            } else {
            	variableRequest.setVariables(new HashSet<>());
            	variableRequest.getVariables().add(Constants.EXTERNAL_COMM);
            }

            RestResponse<VariableResponse> restVariableResponse = retrieveVariables(variableRequest);

            processVariableResponse(restVariableResponse, workingTask, atmTask, variableRequest);
        }

        return atmTask;
    }

    /**
     * Recupera le variabili dal servizio di processo.
     *
     * @param variableRequest la richiesta delle variabili
     * @return la risposta contenente le variabili
     * @throws ErrorException se si verifica un errore nel recupero delle variabili o nel processo
     */
    private RestResponse<VariableResponse> retrieveVariables(VariableRequest variableRequest) {
        RestResponse<VariableResponse> restVariableResponse;
        long start = System.currentTimeMillis();
        try {
            log.info("Retrieving variables: [{}]", variableRequest);
            restVariableResponse = processRestClient.retrieveVariables(variableRequest);
        } catch (WebApplicationException e) {
            log.error("Error calling process service", e);
            if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
            }
            throw new ErrorException(ErrorEnum.PROCESS_ERROR);
        } finally {
            logElapsedTime(RETRIEVE_VARIABLES_LOG_ID, start);
        }
        return restVariableResponse;
    }

    /**
     * Processa la risposta delle variabili e aggiorna il task ATM di conseguenza.
     *
     * @param restVariableResponse la risposta REST contenente le variabili
     * @param workingTask il task attualmente in lavorazione
     * @param atmTask il task ATM da aggiornare
     * @param variableRequest la richiesta delle variabili
     * @throws ErrorException se lo stato della risposta non è OK
     */
    private void processVariableResponse(RestResponse<VariableResponse> restVariableResponse,
                                         Task workingTask, it.pagopa.atmlayer.wf.task.bean.Task atmTask,
                                         VariableRequest variableRequest) {
        if (restVariableResponse.getStatus() == 200) {
            VariableResponse variableResponse = restVariableResponse.getEntity();
            log.info("Retrieved variables: [{}]", variableResponse);
            atmTask.setId(workingTask.getId());
            Map<String, Object> workingVariables = variableResponse.getVariables();

            if (workingVariables != null) {
                Optional.ofNullable(workingVariables.get(Constants.FUNCTION_ID_CONTEXT_LOG))
                        .ifPresent(functionId -> MDC.put(Constants.FUNCTION_ID_CONTEXT_LOG, (String) functionId));

                Optional.ofNullable(workingVariables.get(Constants.EXTERNAL_COMM))
                        .ifPresent(flagExt -> {
                            this.externalComm = (Boolean) flagExt;
                            workingVariables.remove(Constants.EXTERNAL_COMM);
                        });
            }

            manageReceipt(workingVariables, atmTask);
            manageVariables(workingVariables, atmTask, variableRequest);
            updateTemplate(atmTask);
            setButtonInAtmTask(atmTask, variableResponse.getButtons());
        } else if (restVariableResponse.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
        } else {
            throw new ErrorException(ErrorEnum.PROCESS_ERROR);
        }
    }


    private void manageReceipt(Map<String, Object> workingVariables, it.pagopa.atmlayer.wf.task.bean.Task atmTask) {

        if (workingVariables != null && workingVariables.get(Constants.RECEIPT_TEMPLATE) != null) {
            RestResponse<VariableResponse> restVariableResponse = null;
            long start = System.currentTimeMillis();

            try {
                restVariableResponse = processRestClient
                        .retrieveVariables(createVariableRequestForReceipt(workingVariables, atmTask));
            } catch (WebApplicationException e) {

                log.error("Error calling process service", e);
                if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                    throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
                }

                throw new ErrorException(ErrorEnum.PROCESS_ERROR);
            } finally {
                logElapsedTime(RETRIEVE_VARIABLES_LOG_ID, start);
            }

            workingVariables.remove(Constants.RECEIPT_TEMPLATE);

            if (restVariableResponse.getStatus() == 200) {
                try {
                    atmTask.setReceiptTemplate(Base64.getEncoder()
                            .encodeToString(replaceVarValue(restVariableResponse.getEntity().getVariables(),
                                    atmTask.getReceiptTemplate()).getBytes(properties.htmlCharset())));
                } catch (UnsupportedEncodingException e) {
                    log.error(" - ERROR:", e);
                    throw new ErrorException(ErrorEnum.GENERIC_ERROR);
                }
            } else if (restVariableResponse.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
            } else {
                throw new ErrorException(ErrorEnum.PROCESS_ERROR);
            }
        }

    }

    @SuppressWarnings("unchecked")
    private void setButtonInAtmTask(it.pagopa.atmlayer.wf.task.bean.Task atmTask, Map<String, Object> buttons) {
        if (buttons != null) {
            List<Button> buttonsList = new ArrayList<>();
            log.debug("Getting buttons value...");
            for (Map.Entry<String, Object> entry : buttons.entrySet()) {
                Button button = new Button();
                button.setData((Map<String, Object>) entry.getValue());
                button.setId(entry.getKey());
                buttonsList.add(button);
            }
            atmTask.setButtons(buttonsList);
        }
    }

    private DeviceInfo convertDeviceInDeviceInfo(Device device) {
        return DeviceInfo.builder().bankId(device.getBankId()).branchId(device.getBranchId()).code(device.getCode())
                .terminalId(device.getTerminalId()).channel(DeviceType.valueOf(device.getChannel().name()))
                .opTimestamp(device.getOpTimestamp()).build();
    }

    private TaskRequest buildTaskRequest(State state, String transactionId, String functionId) {

        ArrayList<PanInformation> panInfoList = encryptPan(state);

        DeviceInfo deviceInfo = convertDeviceInDeviceInfo(state.getDevice());

        TaskRequest taskRequest = TaskRequest.builder().deviceInfo(deviceInfo).transactionId(transactionId)
                .functionId(functionId).taskId(state.getTaskId()).build();
        taskRequest.setVariables(new HashMap<>());
        // Populate the variable map with peripheral information
        if (state.getDevice().getPeripherals() != null) {
            state.getDevice().getPeripherals().stream()
                    .forEach(per -> taskRequest.getVariables().put(per.getId(), per.getStatus().name()));
        }

        // If additional data is available in the state, include it in the variable map
        if (state.getData() != null) {
            taskRequest.getVariables().putAll(state.getData());
        }
        if (panInfoList != null) {
            taskRequest.getVariables().put(Constants.PAN_INFO, panInfoList);
        }
        return taskRequest;
    }

    private ArrayList<PanInformation> encryptPan(State state) {
        ArrayList<PanInformation> panInformationList = null;
        if (!Utility.nullOrEmpty(state.getPanInfo())) {
            RestResponse<PublicKey> publicKeyResponse = null;
            RSAPublicKey rsaPublicKey = null;
            if (properties.tokenizationIsMock()) {
                rsaPublicKey = Utility.generateRandomRSAPublicKey();
            } else {
                log.info("Calling to get public key.");
                long start = System.currentTimeMillis();
                publicKeyResponse = tokenizationClient.getKey();
                externalComm = true;
                logElapsedTime(CREATE_MAIN_SCENE_LOG_ID, start);

                if (publicKeyResponse.getStatus() == 200) {
                    try {
                        rsaPublicKey = Utility.buildRSAPublicKey(Constants.RSA,publicKeyResponse.getEntity().getModulus());
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        log.error(" - Error during generating RSAPublicKey", e);
                    }
                    log.info("key retrieved successfully.");
                }
            }

            tracePanInfoAndKey(state.getTransactionId(), publicKeyResponse, state.getPanInfo());
            
            if (rsaPublicKey != null) {
                panInformationList = encryptPanInfoList(state.getPanInfo(), rsaPublicKey, publicKeyResponse);
            }
        }
        return panInformationList;
    }

    private ArrayList<PanInformation> encryptPanInfoList(List<PanInfo> panInfoList, RSAPublicKey rsaPublicKey, RestResponse<PublicKey> publicKeyResponse) {
        ArrayList<PanInformation> panInformationList = new ArrayList<>();
        for (PanInfo panInfo : panInfoList) {
            PanInformation panInformation = new PanInformation();
            panInformation.setBankName(panInfo.getBankName());
            panInformation.setCircuits(panInfo.getCircuits());
            panInformation.setLastDigits(panInfo.getPan().substring(panInfo.getPan().length() - 4));

            try {
                RestResponse<GetTokenResponse> getTokenResponse = null;
                if (!properties.tokenizationIsMock()) {
                    getTokenResponse = tokenizationClient.getToken(GetTokenRequest.builder()
                            .encryptedPan(Utility.encryptRSA(panInfo.getPan().getBytes(), rsaPublicKey))
                            .kid(publicKeyResponse.getEntity().getKid()).build());
                    externalComm = true;
                    log.info("GetToken executed successfully. Status code: {}", getTokenResponse.getStatus());
                    panInformation.setPan(getTokenResponse.getEntity().getToken());
                } else {
                    panInformation.setPan(Utility.format(Utility.encryptRSA(panInfo.getPan().getBytes(), rsaPublicKey)));
                }
            } catch (WebApplicationException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                    | IllegalBlockSizeException | BadPaddingException e) {
                log.error("Error during Tokenization: ", e);
            } finally {
                logElapsedTime(GET_TOKEN_TOKENIZER_LOG_ID, System.currentTimeMillis());
            }

            panInformationList.add(panInformation);
        }

        return panInformationList;
    }

    @SuppressWarnings("unchecked")
    private void setVariablesInAtmTask(it.pagopa.atmlayer.wf.task.bean.Task atmTask,
            Map<String, Object> workingVariables) {
        if (workingVariables.get(Constants.ERROR_VARIABLES) instanceof Map) {
            atmTask.setOnError((Map<String, Object>) workingVariables.get(Constants.ERROR_VARIABLES));
            log.debug("Getting error variables: [{}]", workingVariables.remove(Constants.ERROR_VARIABLES));
        }

        if (workingVariables.get(Constants.TIMEOUT_VARIABLES) instanceof Map) {
            atmTask.setOnTimeout((Map<String, Object>) workingVariables.get(Constants.TIMEOUT_VARIABLES));
            log.debug("Getting timeout variables: [{}]", workingVariables.remove(Constants.TIMEOUT_VARIABLES));
        }

        if (workingVariables.get(Constants.TIMEOUT_VALUE) != null) {
            atmTask.setTimeout((int) workingVariables.get(Constants.TIMEOUT_VALUE));
            log.debug("Getting timeout value: [{}]", workingVariables.remove(Constants.TIMEOUT_VALUE));
        }

        if (workingVariables.get(Constants.COMMAND_VARIABLE_VALUE) != null) {
            atmTask.setCommand(Command.valueOf((String) workingVariables.get(Constants.COMMAND_VARIABLE_VALUE)));
            log.debug("Getting command value: [{}]", workingVariables.remove(Constants.COMMAND_VARIABLE_VALUE));
        }

        if (workingVariables.get(Constants.EPP_MODE) != null) {
            atmTask.setEppMode(EppMode.valueOf((String) workingVariables.get(Constants.EPP_MODE)));
            log.debug("Getting eppMode value: [{}]", workingVariables.remove(Constants.EPP_MODE));
        }

        atmTask.setOutcomeVarName((String) workingVariables.get(Constants.OUTCOME_VAR_NAME));
        log.debug("Getting outcomeVarName value: [{}]", workingVariables.remove(Constants.OUTCOME_VAR_NAME));

        if (atmTask.getTemplate() != null) {
            atmTask.getTemplate().setType((String) workingVariables.get(Constants.TEMPLATE_TYPE));
            log.debug("Getting template type value: [{}]", workingVariables.remove(Constants.TEMPLATE_TYPE));
        }

        if (!workingVariables.isEmpty()) {
            log.debug("Getting generic variables...");
            atmTask.setData(workingVariables.get(Constants.DATA_VARIABLES) == null ? new HashMap<String, Object>()
                    : (Map<String, Object>) workingVariables.get(Constants.DATA_VARIABLES));
            workingVariables.remove(Constants.DATA_VARIABLES);
            for (Map.Entry<String, Object> entry : workingVariables.entrySet()) {
                log.info("Variable {}", entry.getKey());
                atmTask.getData().put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

    }

    private VariableRequest createVariableRequestForReceipt(Map<String, Object> variables,
            it.pagopa.atmlayer.wf.task.bean.Task atmTask) {
        VariableRequest variableRequest = new VariableRequest();
        if (variables != null && variables.get(Constants.RECEIPT_TEMPLATE) != null) {
            String receiptTemplateName = (String) variables.get(Constants.RECEIPT_TEMPLATE);
            try {
                log.debug("Finding variables in receipt template...");
                String htmlString = new String(Utility
                        .getFileFromCdn(properties.cdnUrl() + properties.htmlResourcesPath() + receiptTemplateName)
                        .readAllBytes(), properties.htmlCharset());
                Set<String> placeholders = Utility.findStringsByGroup(htmlString, Constants.VARIABLES_REGEX);
                atmTask.setReceiptTemplate(htmlString);
                placeholders.addAll(Utility.getForVar(htmlString));
                log.debug("Placeholders found: {}", placeholders);
                if (placeholders != null && !placeholders.isEmpty()) {
                    log.debug("Number of variables found in receipt template: {}", placeholders.size());
                    variableRequest.setVariables(placeholders);
                }
            } catch (IOException | URISyntaxException e) {
                log.error("- ERROR: File: {} not found!", receiptTemplateName, e);
                throw new ErrorException(ErrorEnum.PROCESS_ERROR);
            }
        }
        variableRequest.setTaskId(atmTask.getId());
        return variableRequest;
    }

    private VariableRequest createVariableRequestForTemplate(Task task, it.pagopa.atmlayer.wf.task.bean.Task atmTask) {
        VariableRequest variableRequest = new VariableRequest();
        if (task.getForm() != null) {
            try {
                log.debug("Finding variables in html form...");
                atmTask.setTemplate(new Template());
                String htmlString = new String(
                        Utility.getFileFromCdn(properties.cdnUrl() + properties.htmlResourcesPath() + task.getForm())
                                .readAllBytes(),
                        properties.htmlCharset());
                Set<String> placeholders = Utility.findStringsByGroup(htmlString, Constants.VARIABLES_REGEX);
                atmTask.getTemplate().setContent(htmlString);
                placeholders.remove(Constants.CDN_PLACEHOLDER);
                placeholders.addAll(Utility.getForVar(htmlString));
                log.debug("Placeholders found: {}", placeholders);
                if (placeholders != null && !placeholders.isEmpty()) {
                    log.info("Number of variables found in html form: {}", placeholders.size());
                    variableRequest.setVariables(placeholders);
                }
                Set<String> buttonList = Utility.getIdOfTag(htmlString, Constants.BUTTON_TAG);
                buttonList.addAll(Utility.getIdOfTag(htmlString, Constants.LI_TAG));
                variableRequest.setButtons(buttonList);
            } catch (IOException | URISyntaxException e) {
                log.error("- ERROR: File: {} not found!", task.getForm(), e);
                throw new ErrorException(ErrorEnum.PROCESS_ERROR);
            }
        }
        variableRequest.setTaskId(task.getId());
        return variableRequest;
    }

    private String replaceVarValue(Map<String, Object> variables, String html) {
        String htmlTemp = html;
        if (htmlTemp != null) {
            log.debug("-----START replacing variables in html-----");

            htmlTemp = parseLoopHtml(variables, html);
            for (Entry<String, Object> value : variables.entrySet()) {
                log.debug("Replacing {} -> {}", "${" + value.getKey() + "}", String.valueOf(value.getValue()));
                htmlTemp = htmlTemp.replace("${" + value.getKey() + "}", String.valueOf(value.getValue()));
            }
            htmlTemp = htmlTemp.replace("${" + Constants.CDN_PLACEHOLDER + "}", properties.cdnUrl());
            Set<String> placeholders = Utility.findStringsByGroup(htmlTemp, Constants.VARIABLES_REGEX);
            if (!placeholders.isEmpty()) {
                log.error("Value not found for placeholders: {}", placeholders);
                throw new ErrorException(ErrorEnum.PROCESS_ERROR);
            }
            log.debug("-----END replacing variables in html-----");
        }
        return htmlTemp;
    }

    private String parseLoopHtml(Map<String, Object> variables, String html) {
        Document doc = Jsoup.parse(html, Parser.xmlParser());
        doc.outputSettings().prettyPrint(false).charset(properties.htmlCharset()).escapeMode(EscapeMode.extended);
        Element forEl = doc.select("for").first();
        if (forEl != null) {
            String obj = forEl.attr("object");
            Set<String> placeholders = Utility.findStringsByGroup(html, Constants.VARIABLES_REGEX);
            placeholders.removeIf(p -> !p.startsWith(obj + "."));

            List<?> list = (List<?>) variables.get(forEl.attr("list"));
            substitute(list, forEl, obj, variables, placeholders);
            forEl.remove();
            doc.html(parseLoopHtml(variables, doc.html()));
        }
        return Utility.escape(doc.html(), properties.escape());
    }

    private void substitute(List<?> list, Element forEl, String obj, Map<String, Object> variables, Set<String> placeholders){
        int i = 0;
        Type listType = new TypeToken<ArrayList<Object>>() {}.getType();
        Gson gson = new Gson();
            if (list != null) {
                for (Object element : list) {
                    JsonElement jsonElement = JsonParser.parseString(Utility.getJson(element));
                    i++;
                    for (Element e : forEl.select("for")) {
                        String listName = e.attr("list");
                        if (listName.startsWith(obj)) {
                            ArrayList<Object> lista = gson.fromJson(getVarPropJsonElement(listName, jsonElement),
                                    listType);
                            variables.put(listName, lista);
                        }
                    }
                    String htmlTemp = parseLoopHtml(variables, forEl.html());
                    htmlTemp = htmlTemp.replace("${" + obj + "}", String.valueOf(element));
                    htmlTemp = htmlTemp.replace("${" + obj + ".i}", String.valueOf(i));

                    for (String var : placeholders) {
                        htmlTemp = htmlTemp.replace("${" + var + "}", getVarProp(var, jsonElement));
                    }
                    forEl.after(htmlTemp);
                }
            }
    }

    private static String getVarProp(String var, JsonElement jsonElement) {
        String[] varProperties = var.split("\\.");
        JsonElement propElement = jsonElement;
        for (int j = 1; j < varProperties.length; j++) {
            JsonObject jsonObject = propElement.getAsJsonObject();
            if (jsonObject.has(varProperties[j])) {
                propElement = jsonObject.get(varProperties[j]);
            } else {
                return "";
            }
        }
        return propElement.getAsString();
    }

    private static JsonElement getVarPropJsonElement(String var, JsonElement jsonElement) {
        String[] varProperties = var.split("\\.");
        JsonElement propElement = jsonElement;
        for (int j = 1; j < varProperties.length; j++) {
            JsonObject jsonObject = propElement.getAsJsonObject();
            if (jsonObject.has(varProperties[j])) {
                propElement = jsonObject.get(varProperties[j]);
            } else {
                return null;
            }
        }
        return propElement;
    }

    private void updateTemplate(it.pagopa.atmlayer.wf.task.bean.Task atmTask) {
        if (atmTask.getTemplate() != null) {
            try {
                atmTask.getTemplate().setContent(Base64.getEncoder()
                        .encodeToString(atmTask.getTemplate().getContent().getBytes(properties.htmlCharset())));
            } catch (UnsupportedEncodingException e) {
                log.error(" - ERROR:", e);
                throw new ErrorException(ErrorEnum.GENERIC_ERROR);
            }

        }
    }

    private void manageVariables(Map<String, Object> workingVariables, it.pagopa.atmlayer.wf.task.bean.Task atmTask,
            VariableRequest variableRequest) {
        if (workingVariables != null) {
            // Replaceing variables with values in template
            if (atmTask.getTemplate() != null) {
                atmTask.getTemplate().setContent(replaceVarValue(workingVariables, atmTask.getTemplate().getContent()));
            }
            setVariablesInAtmTask(atmTask, workingVariables);
            if (variableRequest.getVariables() != null) {
                workingVariables.keySet().removeAll(variableRequest.getVariables());
            }
        }
    }

}
