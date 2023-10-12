package it.pagopa.atmlayer.wf.task.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.bean.Command;
import it.pagopa.atmlayer.wf.task.bean.CommandTask;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.ScreenTask;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.util.Constants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TaskService {

	@Inject
	@RestClient
	ProcessRestClient processProxy;

	@SuppressWarnings("unchecked")
	public Scene buildMain(String functionId, String transactionId, State state) {
		/*
		 * Lettura del modello BPMN via client microservizio
		 */

		/*
		 * TODO: Parsing del file BPMN
		 */

		/*
		 * TODO: Crea l'istanza del processo.
		 */

		/*
		 * TODO: Valutazione delle condizione in base alle variabilidi input
		 */

		/*
		 * TODO: Creazione dell'oggetto Scene
		 */
		Scene scene = new Scene();
		if (transactionId == null) {
			scene.setTransactionId(UUID.randomUUID().toString());
		} else {
			scene.setTransactionId(transactionId);
		}

		RestResponse<TaskResponse> restResponse = processProxy.startProcess(buildTaskRequest(state, transactionId));

		if (restResponse.getStatus() == 200) {
			TaskResponse response = restResponse.getEntity();
			Task workingTask = new Task();
			boolean isScreenTask = false;
			for (Task task : response.getTasks()) {
				workingTask = task;
				if (task.getForm() != null && !task.getForm().isEmpty()) {
					isScreenTask = true;
					break;
				}
			}

			if (isScreenTask) {
				ScreenTask screenTask = new ScreenTask();
				Map<String, Object> variables = workingTask.getVariables();
				if (variables.get(Constants.ERROR_VARIABLES) instanceof Map) {
					screenTask.setOnError((Map<String, String>) variables.get(Constants.ERROR_VARIABLES));
					variables.remove(Constants.ERROR_VARIABLES);
				}

				if (variables.get(Constants.TIMEOUT_VARIABLES) instanceof Map) {
					screenTask.setOnTimeout((Map<String, String>) variables.get(Constants.TIMEOUT_VARIABLES));
					variables.remove(Constants.TIMEOUT_VARIABLES);
				}

				screenTask.setTimeout((int) variables.get(Constants.TIMEOUT_VALUE));
				variables.remove(Constants.TIMEOUT_VALUE);

				if (!variables.isEmpty()) {
					screenTask.setData(variables.get(Constants.DATA_VARIABLES) == null ? new HashMap<String, String>()
							: (Map<String, String>) variables.get(Constants.DATA_VARIABLES));
					variables.remove(Constants.DATA_VARIABLES);
					variables.entrySet().stream()
							.forEach(k -> screenTask.getData().put(k.getKey(), (String) k.getValue()));
				}
				screenTask.setId(workingTask.getId());
				screenTask.setTemplate(workingTask.getForm());
				scene.setScreenTask(screenTask);
			} else {
				CommandTask commandTask = new CommandTask();
				// TODO
			}

		}

		return scene;
	}

	public Scene buildNext(String transactionId) {
		Scene scene = new Scene();
		CommandTask commandTask = new CommandTask();
		commandTask.setCommand(Command.SCAN_BIIL_DATA);
		commandTask.setId("Activity_2");
		Map<String, String> data = new HashMap<>();
		data.put("type", "QRcode");
		commandTask.setData(data);
		commandTask.setOutcomeVarName("scansioneResult");

		data = new HashMap<>();
		data.put("error", "Error on QRcode scanning");
		commandTask.setOnError(data);

		data = new HashMap<>();
		data.put("error", "Timeout on QRcode scanning");
		commandTask.setOnTimeout(data);

		scene.setCommandTask(commandTask);
		scene.setTransactionId(transactionId);

		return scene;
	}

	private DeviceInfo convertDeviceInDeviceInfo(Device device) {
		DeviceInfo deviceInfo = DeviceInfo.builder()
				.bankId(device.getBankId())
				.branchId(device.getBranchId())
				.code(device.getCode())
				.deviceType(DeviceType.valueOf(device.getChannel().name()))
				.opTimestamp(device.getOpTimestamp()).build();
		return deviceInfo;
	}

	private TaskRequest buildTaskRequest(State state, String transactionId) {
		DeviceInfo deviceInfo = convertDeviceInDeviceInfo(state.getDevice());

		TaskRequest taskRequest = new TaskRequest();
		taskRequest.setDeviceInfo(deviceInfo);
		taskRequest.setTransactionId(transactionId);
		Map<String, Object> variables = new HashMap<>();
		state.getDevice().getPeripherals()
				.stream().forEach(per -> variables.put(per.getId(), per.getStatus()));

		taskRequest.setVariables(variables);

		return taskRequest;
	}

}
