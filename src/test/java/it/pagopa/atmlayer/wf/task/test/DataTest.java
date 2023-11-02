package it.pagopa.atmlayer.wf.task.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import it.pagopa.atmlayer.wf.task.bean.Channel;
import it.pagopa.atmlayer.wf.task.bean.Command;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Peripheral;
import it.pagopa.atmlayer.wf.task.bean.PeripheralStatus;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.util.Constants;

public class DataTest {

    public static TaskResponse createTaskResponse(int numberOfTasks) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTasks(new ArrayList<Task>());
        for (int i = 0; i <= numberOfTasks; i++) {
            Task task = new Task();
            task.setId(RandomStringUtils.randomAlphanumeric(10));
            task.setPriority(i);

            taskResponse.getTasks().add(task);
        }

        taskResponse.getTasks().get(0).setForm("riepilogoCommissioni.html");
        Map<String, Object> templateMap = new HashMap<>();
        templateMap.put(Constants.RECEIPT_TEMPLATE, "riepilogoCommissioni.html");
        taskResponse.getTasks().get(0).setVariables(templateMap);
        taskResponse.setTransactionId("1000");

        return taskResponse;
    }

    public static TaskResponse createTaskResponseMissingHtml(int numberOfTasks) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTasks(new ArrayList<Task>());
        for (int i = 0; i <= numberOfTasks; i++) {
            Task task = new Task();
            task.setId(RandomStringUtils.randomAlphanumeric(10));
            task.setPriority(i);

            taskResponse.getTasks().add(task);
        }

        taskResponse.getTasks().get(0).setForm("test.html");
        taskResponse.setTransactionId("1000");

        return taskResponse;
    }

    public static VariableResponse createVariableResponseNoData() {
        VariableResponse variableResponse = new VariableResponse();
        variableResponse.setButtons(new HashMap<String, Object>());
        variableResponse.setVariables(new HashMap<String, Object>());

        Map<String, String> variablesMap = new HashMap<>();
        variablesMap.put("company", "Auriga");

        variableResponse.getVariables().put("company", variablesMap);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", "Auriga");

        Map<String, String> timeoutMap = new HashMap<>();
        timeoutMap.put("error", "Auriga");

        variableResponse.getVariables().put(Constants.ERROR_VARIABLES, errorMap);
        variableResponse.getVariables().put(Constants.TIMEOUT_VARIABLES, timeoutMap);
        variableResponse.getVariables().put("company", "Auriga");
        variableResponse.getVariables().put(Constants.TIMEOUT_VALUE, 50);
        variableResponse.getVariables().put("description", "descrizione");
        variableResponse.getVariables().put("amount", 10000);
        variableResponse.getVariables().put("fee", 2.30);
        variableResponse.getVariables().put("totale", 11.50);
        variableResponse.getVariables().put("variable1", "11.50");
        variableResponse.getVariables().put(Constants.COMMAND_VARIABLE_VALUE, Command.PRINT_RECEIPT.name());

        Map<String, Object> buttonMap = new HashMap<>();
        buttonMap.put("prop1", "value1");
        variableResponse.getButtons().put("button1", errorMap);

        return variableResponse;
    }

    public static VariableResponse createVariableResponseWithData() {
        VariableResponse variableResponse = new VariableResponse();
        variableResponse.setButtons(new HashMap<String, Object>());
        variableResponse.setVariables(new HashMap<String, Object>());
        Map<String, Object> data = new HashMap<>();
        data.put("test1", "data1");
        variableResponse.getVariables().put("data", data);

        return variableResponse;
    }

    public static State createStateRequestStart() {
        List<Peripheral> perList = new ArrayList<>();
        Peripheral per = new Peripheral();
        per.setId("PRINTER");
        per.setName("PRINTER");
        per.setStatus(PeripheralStatus.OK);
        perList.add(per);
        Device deviceInfo = Device.builder().bankId(RandomStringUtils.randomAlphanumeric(5))
                .branchId(RandomStringUtils.randomAlphanumeric(5))
                .channel(Channel.ATM)
                .code(RandomStringUtils.randomNumeric(5))
                .opTimestamp(new Date())
                .peripherals(perList)
                .build();
        State state = new State();
        state.setDevice(deviceInfo);
        Map<String, Object> variablesData = new HashMap<>();
        variablesData.put("var1", "test");
        state.setData(variablesData);
        return state;
    }

    public static State createStateRequestNext() {
        State state = createStateRequestStart();
        state.setTaskId("1");
        return state;
    }
}
