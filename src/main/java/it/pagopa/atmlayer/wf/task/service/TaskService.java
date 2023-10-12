package it.pagopa.atmlayer.wf.task.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import it.pagopa.atmlayer.wf.task.bean.Button;
import it.pagopa.atmlayer.wf.task.bean.Command;
import it.pagopa.atmlayer.wf.task.bean.CommandTask;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Scene;
import it.pagopa.atmlayer.wf.task.bean.ScreenTask;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.client.ProcessProxy;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceInfo;
import it.pagopa.atmlayer.wf.task.client.bean.DeviceType;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.util.Constants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TaskService {

	@Inject
	@RestClient
	ProcessProxy processProxy;

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

		DeviceInfo deviceInfo = convertDeviceInDeviceInfo(state.getDevice());

		TaskRequest taskRequest = new TaskRequest();
		taskRequest.setDeviceInfo(deviceInfo);
		taskRequest.setTransactionId(scene.getTransactionId());
		Map<String, Object> variables = new HashMap<>();
		state.getDevice().getPeripherals()
				.stream().forEach(per -> variables.put(per.getId(), per.getStatus()));

		taskRequest.setVariables(variables);
		RestResponse<TaskResponse> restResponse = processProxy.startProcess(taskRequest);

		if (restResponse.getStatus() == 200) {
			TaskResponse response = restResponse.getEntity();
			if (response.getVariables().containsKey(Constants.SCREEN_TASK_VARIABLE)) {
				ScreenTask screenTask = new ScreenTask();
				// List<KeyPair> variablesList = new ArrayList<>();
				// response.getVariables().entrySet().stream()
				// .forEach(k -> variablesList.add(new KeyPair(k.getKey(), (String)
				// k.getValue())));
				// screenTask.setData(variablesList);

				scene.setScreenTask(screenTask);
				// screenTask.setData();
			}

		}

		ScreenTask screenTask = new ScreenTask();
		screenTask.setTimeout(120);
		screenTask.setTemplate("stampanteKO.html");
		screenTask.setId("Activity_1");
		LinkedList<Button> buttons = new LinkedList<>();
		Button button = new Button();
		button.setId("idButtonProcedere");
		Map<String, String> data = new HashMap<>();
		data.put("procedere", "true");
		button.setData(data);
		buttons.addLast(button);
		screenTask.setButtons(buttons);
		// scene.setCommandTask(new CommandTask(Command.AUTHORIZE, null, null));
		scene.setScreenTask(screenTask);
		data = new HashMap<>();
		data.put("error", "Error on stampanteKO.html");
		screenTask.setOnError(data);

		data = new HashMap<>();
		data.put("error", "timeout on stampanteKO.html");
		screenTask.setOnTimeout(data);
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
		commandTask.setTimeout(120);

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

	public static void main(String[] args) {
		/*
		 * 
		 * List<KeyPair> list = new ArrayList<>();
		 * Map<String, Object> variables = new HashMap<>();
		 * variables.put("Ollare2", "Ollare2");
		 * variables.put("Timeout", "timeout");
		 * variables.put("Ollare3", "Ollare3");
		 * variables.entrySet().stream()
		 * .filter(k -> !k.getKey().equals(Constants.TIMEOUT_VARIABLE))
		 * .forEach(k -> list.add(new KeyPair(k.getKey(), (String) k.getValue())));
		 * variables.entrySet().removeAll(list);
		 * System.out.println("List: " + list + "\nMAP: " + variables);
		 */
	}

}
