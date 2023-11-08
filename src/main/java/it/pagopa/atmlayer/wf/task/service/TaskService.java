package it.pagopa.atmlayer.wf.task.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.bean.Button;
import it.pagopa.atmlayer.wf.task.bean.Command;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.Exceptions.ErrorBean;
import it.pagopa.atmlayer.wf.task.bean.Exceptions.ErrorException;
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
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class TaskService {

	@Inject
	@RestClient
	ProcessRestClient processRestClient;

	@Inject
	Properties properties;

	private static final String VARIABLES_REGEX = "\\$\\{(.*?)\\}";

	private static final String BUTTON_TAG = "button";

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
		if (taskRequest.getTaskId() != null) {
			log.info("Calling next task after for task id: [{}]", taskRequest.getTaskId());
			restTaskResponse = processRestClient.nextTasks(taskRequest);
		} else {
			log.info("Calling start process of function: [{}]", functionId);
			restTaskResponse = processRestClient.startProcess(taskRequest);
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
			for (String key : buttons.keySet()) {
				Button button = new Button();
				button.setData((Map<String, Object>) buttons.get(key));
				button.setId(key);
				buttonsList.add(button);
			}
			atmTask.setButtons(buttonsList);
		}
	}

	/**
	* Constructs and retrieves a task object for further processing based on transaction and state.
	*
	* This method is an overloaded version of the `buildTask` method that omits the function ID,
	* allowing the construction of a task based solely on the transaction ID and the current state.
	* It internally calls the main `buildTask` method with a null function ID and the provided transaction ID and state to simplify the task construction process.
	*
	* @param transactionId The unique identifier for the transaction associated with the task.
	* @param state The current state of the task.
	* @return A task object representing the next available task, or null if no task is available or if there is an error during the process.
	*/
	public it.pagopa.atmlayer.wf.task.bean.Task buildTask(String transactionId, State state) {
		return buildTask(null, transactionId, state);
	}

	public Scene buildNext(String transactionId, State state) {
		Scene scene = new Scene();
		scene.setTask(buildTask(transactionId, state));
		scene.setTransactionId(transactionId);
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
		scene.setTransactionId(generateTransactionId(state.getDevice()));
		scene.setTask(buildTask(functionId, scene.getTransactionId(), state));
		return scene;
	}

	private DeviceInfo convertDeviceInDeviceInfo(Device device) {
		DeviceInfo deviceInfo = DeviceInfo.builder()
				.bankId(device.getBankId())
				.branchId(device.getBranchId())
				.code(device.getCode())
				.terminalId(device.getTerminalId())
				.channel(DeviceType.valueOf(device.getChannel().name()))
				.opTimestamp(device.getOpTimestamp()).build();
		return deviceInfo;
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
		taskRequest.setVariables(new HashMap<String, Object>());
		// Populate the variable map with peripheral information
		state.getDevice().getPeripherals().stream().forEach(
				per -> taskRequest.getVariables().put(per.getId(), per.getStatus().name()));

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
			for (String key : workingVariables.keySet()) {
				log.info("Variable {}", key);
				atmTask.getData().put(key, String.valueOf(workingVariables.get(key)));
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
				/*
				 * String htmlString = new String(
				 * Files.readAllBytes(Paths.get(properties.templatePath() + task.getForm())));
				 */
				String htmlString = new String(getFileAsIOStream(task.getForm()).readAllBytes(),
						properties.htmlCharset());
				List<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				log.debug("Placeholders found: {}", placeholders);
				if (placeholders != null && !placeholders.isEmpty()) {
					log.debug("Number of variables found in html form: {}", placeholders.size());
					variableRequest.setVariables(placeholders);
				}
				variableRequest.setButtons(Utility.getIdOfTag(htmlString, BUTTON_TAG));
			} catch (IOException e) {
				log.error("- ERROR: File: {} not found!", task.getForm());
				throw new ErrorException(ErrorBean.GENERIC_ERROR);
			}
		}
		// Find variables in receipt template
		if (task.getVariables() != null
				&& task.getVariables().get(Constants.RECEIPT_TEMPLATE) != null) {
			try {
				/*
				 * String htmlString = new String(Files.readAllBytes( Paths.get((String)
				 * task.getVariables() .get(Constants.RECEIPT_TEMPLATE))));
				 */
				String htmlString = new String(getFileAsIOStream((String) task.getVariables()
						.get(Constants.RECEIPT_TEMPLATE)).readAllBytes(),
						properties.htmlCharset());
				List<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				if (placeholders != null && !placeholders.isEmpty()) {
					log.debug("Number of variables found in receipt template: {}", placeholders.size());
					variableRequest.setVariables(placeholders);
				}
			} catch (IOException e) {
				log.error("- ERROR: File: {} not found!", task.getForm());
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
			Utility.findStringsByGroup(task.getTemplate(), VARIABLES_REGEX).stream()
					.forEach(var -> {
						Object value = variables.get(var);
						log.info("Var {} replaced -> {}", var, value);
						task.setTemplate(task.getTemplate().replace("${" + var + "}", String.valueOf(value)));
					});
			log.info("-----END replacing variables in html-----");
		}
	}

	/**
	* Generates a unique transaction ID for a device.
	*
	* This method creates a unique transaction ID using the UUID (Universally Unique Identifier) generator.
	* The generated transaction ID is intended to uniquely identify a transaction associated with a specific device.
	*
	* @param device The device for which the transaction ID is being generated.
	* @return A unique transaction ID in UUID format.
	*/
	private String generateTransactionId(Device device) {
		return (device.getBankId()
				+ "-" + (device.getBranchId() != null ? device.getBranchId() : "")
				+ "-" + (device.getCode() != null ? device.getCode() : "")
				+ "-" + (device.getTerminalId() != null ? device.getTerminalId() : "")
				+ "-" + (device.getOpTimestamp().getTime())
				+ "-" + UUID.randomUUID().toString()).substring(0, Constants.TRANSACTION_ID_LENGTH);
	}

	private InputStream getFileAsIOStream(String fileName) {

		InputStream ioStream = null;
		log.info("Getting HTML template [{}] from {}", fileName, properties.htmlResourcesPath());
		try {
			ioStream = new URL(properties.htmlResourcesPath() + fileName).openStream();
		} catch (IOException e) {
			log.error("ERROR: {}", e);
			throw new ErrorException(ErrorBean.GENERIC_ERROR);
		}
		return ioStream;
	}

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
						atmTask.setTemplate(new String(getFileAsIOStream(workingTask.getForm()).readAllBytes(),
								properties.htmlCharset()));
					} catch (IOException e) {
						log.error("File not found {}", workingTask.getForm());
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
			} else {
				throw new ErrorException(ErrorBean.GET_VARIABLES_ERROR);
			}
		}
		return atmTask;
	}

	public static void main(String[] args) throws IOException {
		InputStream input = new URL("https://d2xduy7tbgu2d3.cloudfront.net/files/HTML/datiAvviso.html").openStream();
		System.out.println(new String(input.readAllBytes()));
	}
}
