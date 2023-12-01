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
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
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
		RestResponse<TaskResponse> restTaskResponse = null;
		try {
			log.info("Calling start process: [{}]", taskRequest);
			restTaskResponse = processRestClient.startProcess(taskRequest);
		} catch (WebApplicationException e) {
			log.error("Error calling process service", e);
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
		try {
			log.info("Calling next task: [{}]", taskRequest);
			restTaskResponse = processRestClient.nextTasks(taskRequest);
		} catch (WebApplicationException e) {
			log.error("Error calling process service", e);
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

		if (!response.getTasks().isEmpty()) {
			Task workingTask = response.getTasks().get(0);
			VariableRequest variableRequest = createVariableRequest(workingTask);
			log.info("Calling retrieve variables for task id: [{}]", workingTask.getId());
			RestResponse<VariableResponse> restVariableResponse = null;
			try {
				log.info("Retrieving variables: [{}]", variableRequest);
				restVariableResponse = processRestClient.retrieveVariables(variableRequest);
			} catch (WebApplicationException e) {
				log.error("Error calling process service", e);
				if (e.getResponse().getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
					throw new ErrorException(ErrorEnum.GET_VARIABLES_ERROR);
				}
				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
			}

			if (restVariableResponse.getStatus() == 200) {
				VariableResponse variableResponse = restVariableResponse.getEntity();
				log.info("Retrieved variables: [{}]", variableResponse);
				atmTask = new it.pagopa.atmlayer.wf.task.bean.Task();
				atmTask.setId(workingTask.getId());
				Map<String, Object> workingVariables = variableResponse.getVariables();

				setTemplate(atmTask, workingTask);

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

	private void setReceipt() {
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

		atmTask.setReceiptTemplate((String) workingVariables.get(Constants.RECEIPT_TEMPLATE));
		log.debug("Getting recepitTemplate value: [{}]", workingVariables.remove(Constants.RECEIPT_TEMPLATE));

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

	private VariableRequest createVariableRequest(Task task) {
		VariableRequest variableRequest = new VariableRequest();
		if (task.getForm() != null) {
			try {
				log.debug("Finding variables in html form...");
				String htmlString = new String(
						Utility.getFileFromCdn(properties.cdnUrl() + properties.htmlResourcesPath() + task.getForm())
								.readAllBytes(),
						properties.htmlCharset());
				List<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				log.debug("Placeholders found: {}", placeholders);
				if (placeholders != null && !placeholders.isEmpty()) {
					log.debug("Number of variables found in html form: {}", placeholders.size());
					variableRequest.setVariables(placeholders);
				}
				List<String> buttonList = Utility.getIdOfTag(htmlString, BUTTON_TAG);
				buttonList.addAll(Utility.getIdOfTag(htmlString, LI_TAG));
				variableRequest.setButtons(buttonList);
			} catch (IOException e) {
				log.error("- ERROR: File: {} not found!", task.getForm(), e);
				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
			}
		}
		// Find variables in receipt template
		if (task.getVariables() != null
				&& task.getVariables().get(Constants.RECEIPT_TEMPLATE) != null) {
			try {
				String htmlString = new String(
						Utility.getFileFromCdn(properties.cdnUrl() + properties.htmlResourcesPath()
								+ (String) task.getVariables()
										.get(Constants.RECEIPT_TEMPLATE))
								.readAllBytes(),
						properties.htmlCharset());
				List<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				placeholders.addAll(Utility.getIdOfTag(htmlString, LI_TAG));
				if (placeholders != null && !placeholders.isEmpty()) {
					log.debug("Number of variables found in receipt template: {}", placeholders.size());
					variableRequest.setVariables(placeholders);
				}
			} catch (IOException e) {
				log.error("- ERROR: File: {} not found!", task.getVariables().get(Constants.RECEIPT_TEMPLATE), e);
				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
			}
		}
		variableRequest.setTaskId(task.getId());
		return variableRequest;
	}

	private String replaceVarValue(Map<String, Object> variables, String html) {
		if (html != null) {
			log.info("-----START replacing variables in html-----");
			for (Entry<String, Object> value : variables.entrySet()) {
				html = html.replace("${" + value.getKey() + "}", String.valueOf(value.getValue()));
			}
			html = html.replace("${" + Constants.CDN_PLACEHOLDER + "}", properties.cdnUrl());
			List<String> placeholders = Utility.findStringsByGroup(html, VARIABLES_REGEX);
			if (!placeholders.isEmpty()) {
				log.error("Value not found for placeholders: {}", placeholders);
				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
			}
			log.info("-----END replacing variables in html-----");
		}
		return html;
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
			// Replaceing variables with values in template
			if (variableRequest.getVariables() != null) {
				workingVariables.keySet().removeAll(variableRequest.getVariables());
			}
			setVariablesInAtmTask(atmTask, workingVariables);
			atmTask.setReceiptTemplate(replaceVarValue(workingVariables, atmTask.getReceiptTemplate()));
		}
	}

	private void setTemplate(it.pagopa.atmlayer.wf.task.bean.Task atmTask, Task workingTask) {
		if (workingTask.getForm() != null) {
			try {
				atmTask.setTemplate(new Template());
				atmTask.getTemplate().setContent(
						new String(Utility.getFileFromCdn(properties.cdnUrl()
								+ properties.htmlResourcesPath()
								+ workingTask.getForm()).readAllBytes(),
								properties.htmlCharset()));
			} catch (IOException e) {
				log.error("File not found {}", workingTask.getForm(), e);
				throw new ErrorException(ErrorEnum.PROCESS_ERROR);
			}
		}
	}

}
