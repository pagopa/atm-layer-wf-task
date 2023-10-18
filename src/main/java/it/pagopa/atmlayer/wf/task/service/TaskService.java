package it.pagopa.atmlayer.wf.task.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.bean.Command;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TaskService {

	@Inject
	@RestClient
	ProcessRestClient processRestClient;

	private static final Logger LOG = Logger.getLogger(TaskService.class);

	private static final String VARIABLES_REGEX = "\\$\\{(.*?)\\}";

	private static final String VARIABLES_REPLACE_REGEX = "\\$\\{[^}]+\\}";

	private static final String BUTTON_TAG = "button";

	public it.pagopa.atmlayer.wf.task.bean.Task buildTask(String functionId, String transactionId, State state) {

		TaskRequest taskRequest = buildTaskRequest(state, transactionId, functionId);
		RestResponse<TaskResponse> restTaskResponse = null;
		if (taskRequest.getTaskId() != null) {
			restTaskResponse = processRestClient.nextTasks(taskRequest);
		} else {
			restTaskResponse = processRestClient.startProcess(taskRequest);
		}

		it.pagopa.atmlayer.wf.task.bean.Task atmTask = null;

		if (restTaskResponse != null && restTaskResponse.getStatus() == 200) {
			TaskResponse response = restTaskResponse.getEntity();
			Task workingTask = new Task();
			Collections.sort(response.getTasks(), Comparator.comparingInt(Task::getPriority));

			// Recupero il primo task ordinato per priorità
			if (!response.getTasks().isEmpty()) {
				workingTask = response.getTasks().get(0);
				VariableRequest variableRequest = createVariableRequest(workingTask);

				RestResponse<VariableResponse> restVariableResponse = processRestClient
						.retrieveVariables(variableRequest);

				if (restVariableResponse.getStatus() == 200) {
					VariableResponse variableResponse = restVariableResponse.getEntity();
					atmTask = new it.pagopa.atmlayer.wf.task.bean.Task();
					atmTask.setId(workingTask.getId());

					setVariablesInAtmTask(atmTask, variableResponse.getVariables());

				}
			}

		}

		return atmTask;
	}

	public it.pagopa.atmlayer.wf.task.bean.Task buildTask(String transactionId, State state) {
		return buildTask(null, transactionId, state);
	}

	public Scene buildNext(String transactionId, State state) {
		Scene scene = new Scene();
		scene.setTask(buildTask(transactionId, state));
		scene.setTransactionId(transactionId);
		return scene;
	}

	public Scene buildFirst(String functionId, String transactionId, State state) {
		Scene scene = new Scene();
		if (transactionId == null) {
			scene.setTransactionId(UUID.randomUUID().toString());
			LOG.debug("TransactionId generated: " + scene.getTransactionId());
		} else {
			scene.setTransactionId(transactionId);
		}
		scene.setTask(buildTask(functionId, scene.getTransactionId(), state));
		return scene;
	}

	private DeviceInfo convertDeviceInDeviceInfo(Device device) {
		DeviceInfo deviceInfo = DeviceInfo.builder()
				.bankId(device.getBankId())
				.branchId(device.getBranchId())
				.code(device.getCode())
				.terminalId(device.getTerminalId())
				.deviceType(DeviceType.valueOf(device.getChannel().name()))
				.opTimestamp(device.getOpTimestamp()).build();
		return deviceInfo;
	}

	private TaskRequest buildTaskRequest(State state, String transactionId, String functionId) {
		DeviceInfo deviceInfo = convertDeviceInDeviceInfo(state.getDevice());

		TaskRequest taskRequest = TaskRequest.builder()
				.deviceInfo(deviceInfo)
				.transactionId(transactionId)
				.functionId(functionId)
				.taskId(state.getTaskId()).build();
		taskRequest.setVariables(new HashMap<String, Object>());
		state.getDevice().getPeripherals().stream()
				.forEach(per -> taskRequest.getVariables().put(per.getId(), per.getStatus().name()));
		if (state.getData() != null) {
			taskRequest.getVariables().putAll(state.getData());
		}
		return taskRequest;
	}

	@SuppressWarnings("unchecked")
	private void setVariablesInAtmTask(it.pagopa.atmlayer.wf.task.bean.Task atmTask, Map<String, Object> variables) {
		Map<String, Object> workingVariables = variables;
		if (workingVariables.get(Constants.ERROR_VARIABLES) instanceof Map) {
			LOG.debug("Getting error variables...");
			atmTask.setOnError((Map<String, String>) workingVariables.get(Constants.ERROR_VARIABLES));
			workingVariables.remove(Constants.ERROR_VARIABLES);
		}

		if (workingVariables.get(Constants.TIMEOUT_VARIABLES) instanceof Map) {
			LOG.debug("Getting timeout variables...");
			atmTask.setOnTimeout((Map<String, String>) workingVariables.get(Constants.TIMEOUT_VARIABLES));
			workingVariables.remove(Constants.TIMEOUT_VARIABLES);
		}

		LOG.debug("Getting timout value...");
		atmTask.setTimeout((int) workingVariables.get(Constants.TIMEOUT_VALUE));
		workingVariables.remove(Constants.TIMEOUT_VALUE);

		LOG.debug("Getting command value...");
		if (workingVariables.get(Constants.COMMAND_VARIABLE_VALUE) != null) {
			atmTask.setCommand(Command.valueOf((String) workingVariables.get(Constants.COMMAND_VARIABLE_VALUE)));
			workingVariables.remove(Constants.COMMAND_VARIABLE_VALUE);
		}

		LOG.debug("Getting outcomeVarName value...");
		atmTask.setOutcomeVarName((String) workingVariables.get(Constants.OUTCOME_VAR_NAME));
		workingVariables.remove(Constants.OUTCOME_VAR_NAME);

		LOG.debug("Getting recepitTemplate value...");
		atmTask.setReceiptTemplate((String) workingVariables.get(Constants.RECEIPT_TEMPLATE));
		workingVariables.remove(Constants.RECEIPT_TEMPLATE);

		if (!workingVariables.isEmpty()) {
			LOG.debug("Getting generic variables...");
			atmTask.setData(workingVariables.get(Constants.DATA_VARIABLES) == null ? new HashMap<String, String>()
					: (Map<String, String>) workingVariables.get(Constants.DATA_VARIABLES));
			workingVariables.remove(Constants.DATA_VARIABLES);
			for (String key : workingVariables.keySet()) {
				atmTask.getData().put(key, (String) workingVariables.get(key));
			}
		}
	}

	private VariableRequest createVariableRequest(Task task) {
		VariableRequest variableRequest = new VariableRequest();
		if (task.getForm() != null) {
			try {
				LOG.debug("Finding variables in html form...");
				String htmlString = new String(Files.readAllBytes(Paths.get(task.getForm())));
				List<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				if (placeholders != null && !placeholders.isEmpty()) {
					LOG.debug("Number of variables found in html form: " + placeholders.size());
					variableRequest.setVariables(placeholders);
				}
				variableRequest.setButtons(Utility.getIdOfTag(htmlString, BUTTON_TAG));
			} catch (IOException e) {
				LOG.error("- ERROR", e);
			}
		}
		// Find variables in receipt template
		if (task.getVariables() != null
				&& task.getVariables().get(Constants.RECEIPT_TEMPLATE) != null) {
			try {
				String htmlString = new String(Files.readAllBytes(
						Paths.get((String) task.getVariables().get(Constants.RECEIPT_TEMPLATE))));
				List<String> placeholders = Utility.findStringsByGroup(htmlString, VARIABLES_REGEX);
				if (placeholders != null && !placeholders.isEmpty()) {
					LOG.debug("Number of variables found in receipt template: " + placeholders.size());
					variableRequest.setVariables(placeholders);
				}
			} catch (IOException e) {
				LOG.error("- ERROR", e);
			}
		}
		variableRequest.setTaskId(task.getId());
		return variableRequest;
	}

	@SuppressWarnings("unchecked")
	private void replaceVarValue(it.pagopa.atmlayer.wf.task.bean.Task task, Map<String, Object> variables) {
		for (String var : Utility.findStringsByGroup(task.getTemplate(), VARIABLES_REGEX)) {
			Object value = variables.get(var);
			if (value instanceof Map) {
				value = ((Map<String, Object>) value).get(var);
			}
		}
		/*
		 * placeholders.stream().forEach(var -> {
		 * variables.keySet().stream().filter(variables.get(var) instanceof Map ==
		 * true);
		 * task.getTemplate().replace("${" + var + "}", (String) variables.get(var))}
		 * );
		 */

	}

}
