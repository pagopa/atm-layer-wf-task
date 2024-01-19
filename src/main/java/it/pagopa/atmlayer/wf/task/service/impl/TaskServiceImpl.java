package it.pagopa.atmlayer.wf.task.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.slf4j.MDC;

import it.pagopa.atmlayer.wf.task.bean.Button;
import it.pagopa.atmlayer.wf.task.bean.Device;
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
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.TokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Logging;
import it.pagopa.atmlayer.wf.task.util.Properties;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class TaskServiceImpl implements TaskService {

	@RestClient
	ProcessRestClient processRestClient;
	
	@RestClient
    MilAuthRestClient milAuthRestClient;

	@Inject
	Properties properties;

	private static final String VARIABLES_REGEX = "\\$\\{(.*?)\\}";

	private static final String BUTTON_TAG = "button";

	private static final String LI_TAG = "li";

	@Override
	public Scene buildFirst(String functionId, State state) {	   
	    new Thread(() -> {
	        getToken(state);
	    }).start();
	    //getToken(state);
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
		long stop;

		try {
			log.info("Calling start process: [{}]", taskRequest);
			restTaskResponse = processRestClient.startProcess(taskRequest);

			stop = System.currentTimeMillis();
			Logging.logElapsedTime(Logging.START_PROCESS_LOG_ID, start, stop);
		} catch (WebApplicationException e) {
			stop = System.currentTimeMillis();
			log.error("Error calling process service", e);
			Logging.logElapsedTime(Logging.START_PROCESS_LOG_ID, start, stop);

			if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
				throw new ErrorException(ErrorEnum.GET_TASKS_ERROR);
			}
			throw new ErrorException(ErrorEnum.PROCESS_ERROR);
		}

		
		return manageTaskResponse(restTaskResponse);
	}

	private Scene buildSceneNext(String transactionId, State state) {
		TaskRequest taskRequest = buildTaskRequest(state, transactionId, null);
		RestResponse<TaskResponse> restTaskResponse = null;
		long start = System.currentTimeMillis();
		long stop;

		try {
			log.info("Calling next task: [{}]", taskRequest);
			restTaskResponse = processRestClient.nextTasks(taskRequest);

			stop = System.currentTimeMillis();
			Logging.logElapsedTime(Logging.NEXT_TASKS_LOG_ID, start, stop);
		} catch (WebApplicationException e) {
			stop = System.currentTimeMillis();
			log.error("Error calling process service", e);
			Logging.logElapsedTime(Logging.NEXT_TASKS_LOG_ID, start, stop);

			if (e.getResponse().getStatus() == StatusCode.INTERNAL_SERVER_ERROR) {				
				throw new ErrorException(ErrorEnum.GET_TASKS_ERROR);
			}
			throw new ErrorException(ErrorEnum.PROCESS_ERROR);
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
		long start = System.currentTimeMillis();
		long stop;

		if (!response.getTasks().isEmpty()) {
			Task workingTask = response.getTasks().get(0);
			atmTask = new it.pagopa.atmlayer.wf.task.bean.Task();
			VariableRequest variableRequest = createVariableRequestForTemplate(workingTask, atmTask);
			log.info("Calling retrieve variables for task id: [{}]", workingTask.getId());
			RestResponse<VariableResponse> restVariableResponse = null;
			try {
				log.info("Retrieving variables: [{}]", variableRequest);
				restVariableResponse = processRestClient.retrieveVariables(variableRequest);

				stop = System.currentTimeMillis();
				Logging.logElapsedTime(Logging.RETRIEVE_VARIABLES_LOG_ID, start, stop);
			} catch (WebApplicationException e) {
				stop = System.currentTimeMillis();
				log.error("Error calling process service", e);
				Logging.logElapsedTime(Logging.RETRIEVE_VARIABLES_LOG_ID, start, stop);

				if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
					throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
				}
				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
			}

			if (restVariableResponse.getStatus() == 200) {				

				VariableResponse variableResponse = restVariableResponse.getEntity();
				log.info("Retrieved variables: [{}]", variableResponse);
				atmTask.setId(workingTask.getId());
				Map<String, Object> workingVariables = variableResponse.getVariables();

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
			long stop;

			try {
				restVariableResponse = processRestClient.retrieveVariables(createVariableRequestForReceipt(workingVariables, atmTask));
				stop = System.currentTimeMillis();
				Logging.logElapsedTime(Logging.RETRIEVE_VARIABLES_LOG_ID, start, stop);
			} catch (WebApplicationException e) {
				stop = System.currentTimeMillis();
				log.error("Error calling process service", e);
				Logging.logElapsedTime(Logging.RETRIEVE_VARIABLES_LOG_ID, start, stop);

				if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
					throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
				}

				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
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
		return DeviceInfo.builder()
				.bankId(device.getBankId())
				.branchId(device.getBranchId())
				.code(device.getCode())
				.terminalId(device.getTerminalId())
				.channel(DeviceType.valueOf(device.getChannel().name()))
				.opTimestamp(device.getOpTimestamp()).build();
	}

	private TaskRequest buildTaskRequest(State state, String transactionId, String functionId) {
		DeviceInfo deviceInfo = convertDeviceInDeviceInfo(state.getDevice());

		TaskRequest taskRequest = TaskRequest.builder()
				.deviceInfo(deviceInfo)
				.transactionId(transactionId)
				.functionId(functionId)
				.taskId(state.getTaskId()).build();
		taskRequest.setVariables(new HashMap<>());
		// Populate the variable map with peripheral information
		if (state.getDevice().getPeripherals() != null) {
			state.getDevice().getPeripherals().stream().forEach(
					per -> taskRequest.getVariables().put(per.getId(), per.getStatus().name()));
		}

		// If additional data is available in the state, include it in the variable map
		if (state.getData() != null) {
			taskRequest.getVariables().putAll(state.getData());
		}
		return taskRequest;
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
				String htmlString = new String(
						Utility.getFileFromCdn(properties.cdnUrl() + properties.htmlResourcesPath()
								+ receiptTemplateName).readAllBytes(),
						properties.htmlCharset());
				Set<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				atmTask.setReceiptTemplate(htmlString);
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
				Set<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				atmTask.getTemplate().setContent(htmlString);
				placeholders.remove(Constants.CDN_PLACEHOLDER);
				log.debug("Placeholders found: {}", placeholders);
				if (placeholders != null && !placeholders.isEmpty()) {
					log.info("Number of variables found in html form: {}", placeholders.size());
					variableRequest.setVariables(placeholders);
				}
				Set<String> buttonList = Utility.getIdOfTag(htmlString, BUTTON_TAG);
				buttonList.addAll(Utility.getIdOfTag(htmlString, LI_TAG));
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
			log.info("-----START replacing variables in html-----");
			for (Entry<String, Object> value : variables.entrySet()) {
				log.debug("Replacing {} -> {}", "${" + value.getKey() + "}", String.valueOf(value.getValue()));
				htmlTemp = htmlTemp.replace("${" + value.getKey() + "}", String.valueOf(value.getValue()));
			}
			htmlTemp = htmlTemp.replace("${" + Constants.CDN_PLACEHOLDER + "}", properties.cdnUrl());
			Set<String> placeholders = Utility.findStringsByGroup(htmlTemp, VARIABLES_REGEX);
			if (!placeholders.isEmpty()) {
				log.error("Value not found for placeholders: {}", placeholders);
				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
			}
			log.info("-----END replacing variables in html-----");
		}
		return htmlTemp;
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

    private void getToken(State state) {  
        MDC.put(Constants.TRANSACTION_ID_LOG_CONFIGURATION, state.getTransactionId());
        Device device = state.getDevice();
        log.info("Calling milAuth get Token.");
		long start = System.currentTimeMillis();
		long stop;

        try (RestResponse<TokenResponse> restTokenResponse = milAuthRestClient.getToken( 
                  device.getBankId(), 
                  device.getChannel().name(), 
                  state.getFiscalCode(), 
                  device.getTerminalId(),
                  state.getTransactionId());) {
			
			stop = System.currentTimeMillis();
			Logging.logElapsedTime(Logging.GET_TOKEN_LOG_ID, start, stop);

            if (restTokenResponse!= null) {
                if (restTokenResponse.getStatus() == 200) {
                log.info("Retrieved token: [{}]", restTokenResponse.getEntity().getAccess_token());
                } else {
                    log.warn("Calling milAuth Status: [{}]", restTokenResponse.getStatus());               
                }
            }            
          } catch (WebApplicationException e) {
			stop = System.currentTimeMillis();
			log.error("Error calling milAuth get Token service", e);
			Logging.logElapsedTime(Logging.GET_TOKEN_LOG_ID, start, stop);
          } 
        MDC.remove(Constants.TRANSACTION_ID_LOG_CONFIGURATION);
    }
}
