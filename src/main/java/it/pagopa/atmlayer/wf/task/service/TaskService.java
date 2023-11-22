package it.pagopa.atmlayer.wf.task.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.bean.Button;
import it.pagopa.atmlayer.wf.task.bean.Command;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorBean;
import it.pagopa.atmlayer.wf.task.bean.exceptions.ErrorException;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Properties;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class TaskService {

	@RestClient
	ProcessRestClient processRestClient;

	@Inject
	Properties properties;

	private static final String VARIABLES_REGEX = "\\$\\{(.*?)\\}";

	private static final String BUTTON_TAG = "button";

	private static final String LI_TAG = "li";

	/**
	* Builds and retrieves a task based on the provided parameters.
	*
	* This method constructs a task for a given function, transaction, and state, making a series of REST API calls to the underlying system. 
	* The logic includes determining whether to start a new process or fetch the next task based on the task ID. It handles task response data,
	* variable retrieval, and variable replacement within the task object.
	*
	* @param functionId The unique identifier for the function associated with the task.
	* @param transactionId The unique identifier for the transaction associated with the task.
	* @param state The current state of the task.
	* @return A task object representing the next available task, or null if no task is available or an error occurs during the process.
	*/
	public it.pagopa.atmlayer.wf.task.bean.Task buildTask(String functionId, String transactionId, State state) {

		TaskRequest taskRequest = buildTaskRequest(state, transactionId, functionId);
		RestResponse<TaskResponse> restTaskResponse = null;
		try {
			if (taskRequest.getTaskId() != null) {
				log.info("Calling next task after for task id: [{}]", taskRequest.getTaskId());
				restTaskResponse = processRestClient.nextTasks(taskRequest);
			} else {
				log.info("Calling start process of function: [{}]", functionId);
				restTaskResponse = processRestClient.startProcess(taskRequest);
			}
		} catch (WebApplicationException e) {
			log.error("Error calling process service", e);
			throw new ErrorException(ErrorBean.GET_TASKS_ERROR);
		}
		if (restTaskResponse.getStatus() == 200) {
			return manageOkResponse(restTaskResponse.getEntity());
		} else {
			throw new ErrorException(ErrorBean.GET_TASKS_ERROR);
		}
	}

	/**
	* Sets the buttons for the given ATM task based on the provided button data.
	*
	* This method populates the list of buttons within an ATM task object by processing the provided button data.
	* It iterates through the map of buttons, creates Button objects for each button, and assigns the corresponding data and identifier. 
	* The resulting list of buttons is then set in the ATM task object.
	*
	* @param atmTask The ATM task object for which buttons are being set.
	* @param buttons A map containing button data where each key represents a button identifier and its associated value is the button's data.
	*/
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

	public Scene buildNext(String transactionId, State state) {
		Scene scene = new Scene();
		scene.setTransactionId(transactionId);
		scene.setTask(buildTask(null, transactionId, state));
		return scene;
	}

	/**
	* Constructs and returns a new Scene based on the provided transaction and state.
	*
	* This method creates a new Scene object by calling the `buildTask` method to generate a task and sets the transaction ID for the scene.
	* The resulting Scene represents the next scene in the process, including the associated task and transaction ID.
	*
	* @param transactionId The unique identifier for the transaction related to the scene.
	* @param state The current state of the task.
	* @return A Scene object containing the task and transaction information for the next scene.
	*/
	public Scene buildFirst(String functionId, State state) {
		Scene scene = new Scene();
		scene.setTransactionId(state.getTransactionId());
		scene.setTask(buildTask(functionId, scene.getTransactionId(), state));
		return scene;
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

	/**
	* Constructs a TaskRequest object for a new task with the specified state, transaction, and function details.
	*
	* This method creates a TaskRequest object used to initiate a new task, incorporating information such as the device, transaction ID, function ID, task ID, and variables.
	* It first converts the device information into DeviceInfo, then constructs the TaskRequest with the provided parameters and initializes an empty variable map.
	* It populates the variable map with peripheral information and additional data from the state, if available.
	*
	* @param state The current state for the task.
	* @param transactionId The unique identifier for the transaction associated with the task request.
	* @param functionId The unique identifier for the function related to the task request.
	* @return A TaskRequest object configured with the specified task details and variables.
	*/
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

	/**
	* Sets variables within an ATM task object based on the provided working variables.
	*
	* This method is responsible for populating various variables and settings within an ATM task object using the working variables provided. 
	* It retrieves and assigns values for error variables, timeout variables, timeout value, command value, outcome variable name, receipt template, and generic data variables. 
	* Any assigned variables are removed from the working variables map to ensure they are not duplicated within the ATM task object.
	*
	* @param atmTask The ATM task object to which variables are being assigned.
	* @param workingVariables The map of working variables containing variable names and their corresponding values.
	*/
	@SuppressWarnings("unchecked")
	private void setVariablesInAtmTask(it.pagopa.atmlayer.wf.task.bean.Task atmTask,
			Map<String, Object> workingVariables) {
		if (workingVariables.get(Constants.ERROR_VARIABLES) instanceof Map) {
			log.debug("Getting error variables...");
			atmTask.setOnError((Map<String, Object>) workingVariables.get(Constants.ERROR_VARIABLES));
			workingVariables.remove(Constants.ERROR_VARIABLES);
		}

		if (workingVariables.get(Constants.TIMEOUT_VARIABLES) instanceof Map) {
			log.debug("Getting timeout variables...");
			atmTask.setOnTimeout((Map<String, Object>) workingVariables.get(Constants.TIMEOUT_VARIABLES));
			workingVariables.remove(Constants.TIMEOUT_VARIABLES);
		}

		if (workingVariables.get(Constants.TIMEOUT_VALUE) != null) {
			log.debug("Getting timeout value...");
			atmTask.setTimeout((int) workingVariables.get(Constants.TIMEOUT_VALUE));
			workingVariables.remove(Constants.TIMEOUT_VALUE);
		}

		if (workingVariables.get(Constants.COMMAND_VARIABLE_VALUE) != null) {
			log.debug("Getting command value...");
			atmTask.setCommand(Command.valueOf((String) workingVariables.get(Constants.COMMAND_VARIABLE_VALUE)));
			workingVariables.remove(Constants.COMMAND_VARIABLE_VALUE);
		}

		log.debug("Getting outcomeVarName value...");
		atmTask.setOutcomeVarName((String) workingVariables.get(Constants.OUTCOME_VAR_NAME));
		workingVariables.remove(Constants.OUTCOME_VAR_NAME);

		log.debug("Getting recepitTemplate value...");
		atmTask.setReceiptTemplate((String) workingVariables.get(Constants.RECEIPT_TEMPLATE));
		workingVariables.remove(Constants.RECEIPT_TEMPLATE);

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

	/**
	* Creates a VariableRequest object based on the information extracted from the provided task.
	*
	* This method generates a VariableRequest object used to retrieve variables for a task.
	* It examines the HTML form associated with the task to find placeholders that match a predefined regular expression.
	* These placeholders are considered as variables, and if found, they are added to the VariableRequest's variable list.
	* Additionally, any buttons in the HTML form are identified and included in the VariableRequest. 
	* If a receipt template is provided in the task variables, a similar process is applied to find variables within it. 
	* The resulting VariableRequest object is associated with the task's ID.
	*
	* @param task The task from which variables are being extracted.
	* @return A VariableRequest object configured with the identified variables, buttons, and task ID.
	*/
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
				throw new ErrorException(ErrorBean.GENERIC_ERROR);
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
				throw new ErrorException(ErrorBean.GENERIC_ERROR);
			}
		}
		variableRequest.setTaskId(task.getId());
		return variableRequest;
	}

	/**
	* Replaces variable values within the HTML template of the given ATM task.
	*
	* This method is responsible for replacing variable placeholders in the HTML template of the ATM task with their corresponding values.
	* It iterates through the template, identifies variables based on a predefined regular expression, retrieves their values from the provided map of variables,
	* and replaces the placeholders with the values.
	*
	* @param task The ATM task with an HTML template where variable replacements are performed.
	* @param variables A map containing variable names as keys and their corresponding values.
	*/
	private void replaceVarValue(it.pagopa.atmlayer.wf.task.bean.Task task, Map<String, Object> variables) {
		if (task.getTemplate() != null) {
			log.info("-----START replacing variables in html-----");

			variables.entrySet().stream().forEach(value -> {
				for (String variable : Utility.findStringsByGroup(task.getTemplate(), VARIABLES_REGEX)) {
					if (value.getKey().equals(variable)) {
						log.info("Var {} replaced -> {}", variable, value.getValue());
						task.setTemplate(
								task.getTemplate().replace("${" + variable + "}", String.valueOf(value.getValue())));
						break;
					}
				}
			});

			task.setTemplate(
					task.getTemplate().replace("${" + Constants.CDN_PLACEHOLDER + "}", properties.cdnUrl()));
			List<String> placeholders = Utility.findStringsByGroup(task.getTemplate(), VARIABLES_REGEX);
			if (!placeholders.isEmpty()) {
				log.error("Value not found for placeholders: {}", placeholders);
				throw new ErrorException(ErrorBean.GENERIC_ERROR);
			}
			log.info("-----END replacing variables in html-----");
		}
	}

	/**
	* Processes a successful response and manages the resulting Task object.
	*
	* This method takes a TaskResponse as input, sorts the tasks by priority, retrieves variables for the highest-priority task,
	* and constructs an it.pagopa.atmlayer.wf.task.bean.Task based on the response data. 
	* he constructed Task object may include template content, variable values, and buttons.
	*
	* @param response The TaskResponse containing information about tasks and their priorities.
	* @return An it.pagopa.atmlayer.wf.task.bean.Task object representing the highest-priority task with associated data. Returns null if there are no tasks in the response.
	* @throws ErrorException If an error occurs during the processing of the response or any required data retrieval, 
	* an ErrorException is thrown with appropriate error codes, such as 'GENERIC_ERROR' or 'GET_VARIABLES_ERROR'.
	*/
	private it.pagopa.atmlayer.wf.task.bean.Task manageOkResponse(TaskResponse response) {
		it.pagopa.atmlayer.wf.task.bean.Task atmTask = null;
		Collections.sort(response.getTasks(), Comparator.comparingInt(Task::getPriority));

		// Recupero il primo task ordinato per priorità
		if (!response.getTasks().isEmpty()) {
			Task workingTask = response.getTasks().get(0);
			VariableRequest variableRequest = createVariableRequest(workingTask);
			log.info("Calling retrieve variables for task id: [{}]", workingTask.getId());
			RestResponse<VariableResponse> restVariableResponse = processRestClient.retrieveVariables(variableRequest);

			if (restVariableResponse.getStatus() == 200) {
				VariableResponse variableResponse = restVariableResponse.getEntity();
				atmTask = new it.pagopa.atmlayer.wf.task.bean.Task();
				atmTask.setId(workingTask.getId());
				Map<String, Object> workingVariables = variableResponse.getVariables();

				if (workingTask.getForm() != null) {
					try {
						atmTask.setTemplate(
								new String(
										Utility.getFileFromCdn(properties.cdnUrl()
												+ properties.htmlResourcesPath()
												+ workingTask.getForm()).readAllBytes(),
										properties.htmlCharset()));
					} catch (IOException e) {
						log.error("File not found {}", workingTask.getForm(), e);
						throw new ErrorException(ErrorBean.GENERIC_ERROR);
					}
				}
				if (workingVariables != null) {
					// Replaceing variables with values
					replaceVarValue(atmTask, workingVariables);
					if (variableRequest.getVariables() != null) {
						workingVariables.keySet().removeAll(variableRequest.getVariables());
					}
					setVariablesInAtmTask(atmTask, workingVariables);
				}
				if (atmTask.getTemplate() != null) {
					try {
						atmTask.setTemplate(Base64.getEncoder()
								.encodeToString(atmTask.getTemplate().getBytes(properties.htmlCharset())));
					} catch (UnsupportedEncodingException e) {
						log.error(" - ERROR:", e);
						throw new ErrorException(ErrorBean.GENERIC_ERROR);
					}

				}
				setButtonInAtmTask(atmTask, variableResponse.getButtons());
			} else if (restVariableResponse.getStatus() == 100) {
				throw new ErrorException(ErrorBean.PROCESS_STILL_RUNNING);
			} else {
				throw new ErrorException(ErrorBean.GET_VARIABLES_ERROR);
			}
		}
		return atmTask;
	}
}
