package it.pagopa.atmlayer.wf.task.service.impl;

import java.io.IOException;
import java.io.InputStream;
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
import it.pagopa.atmlayer.wf.task.client.process.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.process.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.process.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.process.bean.Task;
import it.pagopa.atmlayer.wf.task.client.process.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.process.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.process.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.process.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.service.TaskService;
import it.pagopa.atmlayer.wf.task.util.Constants;
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

	@Inject
	Properties properties;

	private static final String VARIABLES_REGEX = "\\$\\{(.*?)\\}";

	private static final String BUTTON_TAG = "button";

	private static final String LI_TAG = "li";

	@Override
	public Scene buildFirst(String functionId, State state) {
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
		Scene result = new Scene();
		log.info("Calling start process: [{}]", taskRequest);		
		try (RestResponse<TaskResponse> restTaskResponse = processRestClient.startProcess(taskRequest)){
		    result = manageTaskResponse(restTaskResponse);
		} catch (WebApplicationException e) {
			log.error("Error calling process service", e);
			if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
			    taskError();
			}
			processError();
		}
		return result;
	}

	private Scene buildSceneNext(String transactionId, State state) {
		TaskRequest taskRequest = buildTaskRequest(state, transactionId, null);
		Scene result = new Scene();
		log.info("Calling next task: [{}]", taskRequest);
		try (RestResponse<TaskResponse> restTaskResponse = processRestClient.nextTasks(taskRequest);){
		     result = manageTaskResponse(restTaskResponse);
		} catch (WebApplicationException e) {
			log.error("Error calling process service", e);
			if (e.getResponse().getStatus() == StatusCode.INTERNAL_SERVER_ERROR) {
				taskError();
			}
			processError();
		}
		return result;
	}

	private Scene manageTaskResponse(RestResponse<TaskResponse> restTaskResponse) {
		Scene scene = new Scene();
		if (restTaskResponse.getStatus() == 200) {
			log.info("Retrieved process: [{}]", restTaskResponse.getEntity());
			scene.setOutcome(new OutcomeResponse(OutcomeEnum.OK));
			scene.setTask(manageOkResponse(restTaskResponse.getEntity()));			
		} else if (restTaskResponse.getStatus() == 202) {
			log.info("Retrieved process: [{}]", restTaskResponse.getEntity());
			scene.setOutcome(new OutcomeResponse(OutcomeEnum.PROCESSING));
		} else {
		    taskError();
		}
		return scene;
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
			log.info("Retrieving variables: [{}]", variableRequest);
			try (RestResponse<VariableResponse> restVariableResponse = processRestClient.retrieveVariables(variableRequest);){
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
    			    getVariablesError();
    			} else {
    				processError();
    			}
			} catch (WebApplicationException e) {
				log.error("Error calling process service", e);
				if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {					
					getVariablesError();
				}
				processError();
			}
			
		}
		return atmTask;
	}

	private void manageReceipt(Map<String, Object> workingVariables, it.pagopa.atmlayer.wf.task.bean.Task atmTask) {

		if (workingVariables != null && workingVariables.get(Constants.RECEIPT_TEMPLATE) != null) {
		    VariableRequest  variableRequest = createVariableRequestForReceipt(workingVariables, atmTask);
			try (RestResponse<VariableResponse> restVariableResponse = processRestClient.retrieveVariables(variableRequest);){
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
	                getVariablesError();
	            } else {
	                processError();
	            }
			} catch (WebApplicationException e) {
				log.error("Error calling process service", e);
				if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
				    getVariablesError();
				}
				processError();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void setButtonInAtmTask(it.pagopa.atmlayer.wf.task.bean.Task atmTask, Map<String, Object> buttons) {
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

	private static DeviceInfo convertDeviceInDeviceInfo(Device device) {
		return DeviceInfo.builder()
				.bankId(device.getBankId())
				.branchId(device.getBranchId())
				.code(device.getCode())
				.terminalId(device.getTerminalId())
				.channel(DeviceType.valueOf(device.getChannel().name()))
				.opTimestamp(device.getOpTimestamp()).build();
	}

	private static TaskRequest buildTaskRequest(State state, String transactionId, String functionId) {
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
	private static void setVariablesInAtmTask(it.pagopa.atmlayer.wf.task.bean.Task atmTask,
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
			atmTask.setTimeout( (int) workingVariables.get(Constants.TIMEOUT_VALUE));
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
			try (InputStream is = Utility.getFileFromCdn(properties.cdnUrl() + properties.htmlResourcesPath()+ receiptTemplateName);){
				log.debug("Finding variables in receipt template...");
				String htmlString = new String(is.readAllBytes(),properties.htmlCharset());
				Set<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				atmTask.setReceiptTemplate(htmlString);
				log.debug("Placeholders found: {}", placeholders);
				if (placeholders != null && !placeholders.isEmpty()) {
					log.debug("Number of variables found in receipt template: {}", placeholders.size());
					variableRequest.setVariables(placeholders);
				}
			} catch (IOException e) {
				log.error("- ERROR: File: {} not found!", receiptTemplateName, e);
				processError();
			}
		}
		variableRequest.setTaskId(atmTask.getId());
		return variableRequest;
	}

	private VariableRequest createVariableRequestForTemplate(Task task, it.pagopa.atmlayer.wf.task.bean.Task atmTask) {
		VariableRequest variableRequest = new VariableRequest();
		if (task.getForm() != null) {
			try (InputStream is = Utility.getFileFromCdn(properties.cdnUrl() + properties.htmlResourcesPath() + task.getForm());){
				log.debug("Finding variables in html form...");
				atmTask.setTemplate(new Template());
				String htmlString = new String(is.readAllBytes(), properties.htmlCharset());
				Set<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				placeholders.remove(Constants.CDN_PLACEHOLDER) ;
				atmTask.getTemplate().setContent(htmlString);
				log.debug("Placeholders found: {}", placeholders);
				if (!placeholders.isEmpty()) {
					log.debug("Number of variables found in html form: {}", placeholders.size());
					variableRequest.setVariables(placeholders);
				}
				Set<String> buttonList = Utility.getIdOfTag(htmlString, BUTTON_TAG);
				buttonList.addAll(Utility.getIdOfTag(htmlString, LI_TAG));
				variableRequest.setButtons(buttonList);
			} catch (IOException e) {
				log.error("- ERROR: File: {} not found!", task.getForm(), e);
				processError();
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
				processError();
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
	
    private static void processError() {
        throw new ErrorException(ErrorEnum.PROCESS_ERROR);
    }
    
    private static void taskError() {
        throw new ErrorException(ErrorEnum.GET_TASKS_ERROR);
    }
    
    private static void getVariablesError() {
        throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
    }
    

}
