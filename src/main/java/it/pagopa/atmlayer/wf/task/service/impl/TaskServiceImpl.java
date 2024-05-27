package it.pagopa.atmlayer.wf.task.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import it.pagopa.atmlayer.wf.task.client.MilAuthRestClient;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.TokenizationRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.GetTokenRequest;
import it.pagopa.atmlayer.wf.task.client.bean.GetTokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.PanInformation;
import it.pagopa.atmlayer.wf.task.client.bean.PublicKey;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.TokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import it.pagopa.atmlayer.wf.task.util.CommonLogic;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class TaskServiceImpl extends CommonLogic implements TaskService {

    @RestClient
    ProcessRestClient processRestClient;

    @RestClient
    MilAuthRestClient milAuthRestClient;

    @RestClient
    TokenizationRestClient tokenizationClient;

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

        state.getData().put("millAccessToken", getToken(state));
        Scene scene = buildSceneStart(functionId, state.getTransactionId(), state);
        scene.setTransactionId(state.getTransactionId());
        return scene;
    }

    @Override
    public Scene buildNext(String transactionId, State state) {
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
            log.info("Retrieved process: [{}]", restTaskResponse.getEntity());
            scene.setOutcome(new OutcomeResponse(OutcomeEnum.PROCESSING));
            return scene;
        } else {
            throw new ErrorException(ErrorEnum.GET_TASKS_ERROR);
        }
    }

    private it.pagopa.atmlayer.wf.task.bean.Task manageOkResponse(TaskResponse response) {
        it.pagopa.atmlayer.wf.task.bean.Task atmTask = null;
        // Recupero il primo task ordinato per priorità
        Collections.sort(response.getTasks(), Comparator.comparingInt(Task::getPriority));

        if (!response.getTasks().isEmpty()) {
            Task workingTask = response.getTasks().get(0);
            atmTask = new it.pagopa.atmlayer.wf.task.bean.Task();
            VariableRequest variableRequest = createVariableRequestForTemplate(workingTask, atmTask);
            log.info("Calling retrieve variables for task id: [{}]", workingTask.getId());
            RestResponse<VariableResponse> restVariableResponse = null;
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

            if (restVariableResponse.getStatus() == 200) {

                VariableResponse variableResponse = restVariableResponse.getEntity();
                log.info("Retrieved variables: [{}]", variableResponse);
                atmTask.setId(workingTask.getId());
                Map<String, Object> workingVariables = variableResponse.getVariables();

                // Aggiungo al contesto dei log la functionId
                if (workingVariables != null && workingVariables.get(Constants.FUNCTION_ID_CONTEXT_LOG) != null) {
                    MDC.put(Constants.FUNCTION_ID_CONTEXT_LOG,
                            (String) workingVariables.get(Constants.FUNCTION_ID_CONTEXT_LOG));
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
        return atmTask;
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
            } catch (IOException e) {
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
            } catch (IOException e) {
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
            handleForElement(variables, forEl);
            forEl.remove();
            doc.html(parseLoopHtml(variables, doc.html()));
        }
        return Utility.escape(doc.html(), properties.escape());
    }
    
    private void handleForElement(Map<String, Object> variables, Element forEl) {
        String obj = forEl.attr("object");
        String listAttr = forEl.attr("list");
        Set<String> placeholders = Utility.findStringsByGroup(forEl.html(), Constants.VARIABLES_REGEX);
        placeholders.removeIf(p -> !p.startsWith(obj + "."));
        List<?> list = (List<?>) variables.get(listAttr);
        
        if (list != null) {
            int i = 0;
            Type listType = new TypeToken<ArrayList<Object>>() {}.getType();
            
            for (Object element : list) {
                JsonElement jsonElement = JsonParser.parseString(Utility.getJson(element));
                i++;
                handleInnerForElements(variables, forEl, obj, jsonElement, i, listType);
                String htmlTemp = parseLoopHtml(variables, forEl.html());
                replacePlaceholders(htmlTemp, obj, element, i, placeholders, jsonElement);
                forEl.after(htmlTemp);
            }
        }
    }
    
    private void handleInnerForElements(Map<String, Object> variables, Element forEl, String obj, JsonElement jsonElement, int i, Type listType) {
        Gson gson = new Gson();
        for (Element e : forEl.select("for")) {
            String listName = e.attr("list");
            if (listName.startsWith(obj)) {
                ArrayList<Object> lista = gson.fromJson(getVarPropJsonElement(listName, jsonElement), listType);
                variables.put(listName, lista);
            }
        }
    }
    
    private void replacePlaceholders(String html, String obj, Object element, int i, Set<String> placeholders, JsonElement jsonElement) {
        html = html.replace("${" + obj + "}", String.valueOf(element));
        html = html.replace("${" + obj + ".i}", String.valueOf(i));
        for (String var : placeholders) {
            html = html.replace("${" + var + "}", getVarProp(var, jsonElement));
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

    /**
     * Return the token created and save it in Redis cache managed by MilAuth.
     * 
     * @param state
     */
    private String getToken(State state) {
        Device device = state.getDevice();
        log.info("Calling milAuth get Token.");
        String token = null;
        long start = System.currentTimeMillis();

        try (RestResponse<TokenResponse> restTokenResponse = milAuthRestClient.getToken(device.getBankId(),
                device.getChannel().name(), state.getFiscalCode(), device.getTerminalId(), state.getTransactionId());) {

            if (restTokenResponse != null) {
                if (restTokenResponse.getStatus() == 200) {
                    token = restTokenResponse.getEntity().getAccess_token();
                    log.info("Retrieved token: [{}]", token);
                } else {
                    log.warn("Calling milAuth Status: [{}]", restTokenResponse.getStatus());
                }
            }
            traceMilAuthClientComm(state, device, restTokenResponse);
        } catch (WebApplicationException e) {
            log.error("Error calling milAuth get Token service", e);
        } finally {
            logElapsedTime(GET_TOKEN_LOG_ID, start);
        }
        return token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteToken(State state) {
        Device device = state.getDevice();
        log.info("Calling milAuth delete Token.");
        long start = System.currentTimeMillis();

        try {
            RestResponse<Object> response = milAuthRestClient.deleteToken(device.getBankId(),
                    device.getChannel().name(), device.getTerminalId(), state.getTransactionId());
            log.info("Token deleted correctly. Status code: {}", response.getStatus());
        } catch (WebApplicationException e) {
            log.warn("MilAuth error in delete Token service", e);
            switch (e.getResponse().getStatus()) {
                case RestResponse.StatusCode.NOT_FOUND -> log.warn("MilAuth error. Token not present in cache.");
                case RestResponse.StatusCode.INTERNAL_SERVER_ERROR ->
                    log.warn("MilAuth error. Redis unavailable or a generic error occured.");
                default -> log.warn("Delete token response with an unknown status {}", e.getResponse().getStatus());
            }
        } finally {
            logElapsedTime(DELETE_TOKEN_LOG_ID, start);
        }

    }

}