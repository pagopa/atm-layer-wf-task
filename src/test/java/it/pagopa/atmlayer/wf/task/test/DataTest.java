package it.pagopa.atmlayer.wf.task.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Peripheral;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.enumartive.Channel;
import it.pagopa.atmlayer.wf.task.bean.enumartive.Command;
import it.pagopa.atmlayer.wf.task.bean.enumartive.EppMode;
import it.pagopa.atmlayer.wf.task.bean.enumartive.PeripheralStatus;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.util.Constants;
import it.pagopa.atmlayer.wf.task.util.Utility;

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

    public static TaskResponse createTaskResponseMissingReceipt(int numberOfTasks) {
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
        templateMap.put(Constants.RECEIPT_TEMPLATE, "aa.html");
        taskResponse.getTasks().get(0).setVariables(templateMap);
        taskResponse.setTransactionId("1000");

        return taskResponse;
    }

    public static TaskResponse createTaskResponseNoForm(int numberOfTasks) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTasks(new ArrayList<Task>());
        for (int i = 0; i <= numberOfTasks; i++) {
            Task task = new Task();
            task.setId(RandomStringUtils.randomAlphanumeric(10));
            task.setPriority(i);

            taskResponse.getTasks().add(task);
        }

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
        variableResponse.getVariables().put(Constants.TIMEOUT_VALUE, 50);
        variableResponse.getVariables().put(Constants.EPP_MODE, EppMode.DATA.name());
        variableResponse.getVariables().put("company", "Auriga");
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
        variableResponse.getVariables().put("company", "Auriga");
        variableResponse.getVariables().put("description", "descrizione");
        variableResponse.getVariables().put("amount", 10000);
        variableResponse.getVariables().put("fee", 2.30);
        variableResponse.getVariables().put("totale", 2.30);

        return variableResponse;
    }

    public static VariableResponse createVariableResponseNoButtons() {
        VariableResponse variableResponse = new VariableResponse();
        return variableResponse;
    }

    public static State createStateRequestStart() {
        List<Peripheral> perList = new ArrayList<>();
        Peripheral per = new Peripheral();
        per.setId("PRINTER");
        per.setName("PRINTER");
        per.setStatus(PeripheralStatus.OK);
        perList.add(per);
        Device deviceInfo = Device.builder().bankId("00001")
                .branchId("0002")
                .channel(Channel.ATM)
                .code("12345")
                .terminalId("1234567890")
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

    public static State createStateRequestStartWithoutDeviceData() {
        List<Peripheral> perList = new ArrayList<>();
        Peripheral per = new Peripheral();
        per.setId("PRINTER");
        per.setName("PRINTER");
        per.setStatus(PeripheralStatus.OK);
        perList.add(per);
        Device deviceInfo = Device.builder().bankId("00001")
                .channel(Channel.ATM)
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

    public static State createStateRequestNextNoBranchId() {
        State state = createStateRequestNext();
        state.getDevice().setBranchId(null);
        return state;
    }

    public static State createStateRequestNextNoCode() {
        State state = createStateRequestNext();
        state.getDevice().setCode(null);
        return state;
    }

    public static State createStateRequestNextNoTerminalId() {
        State state = createStateRequestNext();
        state.getDevice().setTerminalId(null);
        return state;
    }

    public static State createStateRequestNoPeripheral() {
        State state = createStateRequestStart();
        state.getDevice().setPeripherals(null);
        return state;
    }

    public static State createStateRequestNoData() {
        State state = createStateRequestStart();
        state.setData(null);
        return state;
    }

    public static TaskResponse createTaskResponseNoTasks() {
        return TaskResponse.builder().tasks(new ArrayList<Task>()).build();
    }

    public static VariableResponse createvaVariableResponseDefaultVariables() {
        VariableResponse varResponse = createVariableResponseWithData();
        varResponse.setVariables(new HashMap<String, Object>());
        varResponse.getVariables().put(Constants.ERROR_VARIABLES, new HashMap<>());
        varResponse.getVariables().put(Constants.TIMEOUT_VARIABLES, new HashMap<>());
        varResponse.getVariables().put(Constants.TIMEOUT_VALUE, 1);
        varResponse.getVariables().put(Constants.COMMAND_VARIABLE_VALUE, "END");
        varResponse.getVariables().put(Constants.OUTCOME_VAR_NAME, "a");
        varResponse.getVariables().put(Constants.RECEIPT_TEMPLATE, "a.html");
        varResponse.getVariables().put(Constants.TEMPLATE_TYPE, "INFO");
        varResponse.getVariables().put("company", "Auriga");
        varResponse.getVariables().put("description", "descrizione");
        varResponse.getVariables().put("amount", 10000);
        varResponse.getVariables().put("fee", 2.30);
        varResponse.getVariables().put("totale", 11.50);

        return varResponse;
    }

    public static TaskResponse createTaskResponseNoVariablesRequest(int numberOfTasks) {
        TaskResponse taskResponse = createTaskResponse(numberOfTasks);
        taskResponse.getTasks().get(0).setForm("arrivederci.html");
        taskResponse.getTasks().get(0).setVariables(null);
        return taskResponse;
    }

    public static TaskResponse createTaskResponseEndProcess() {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTasks(new ArrayList<Task>());
        taskResponse.setTransactionId("1000");
        return taskResponse;
    }

    public static void main(String[] args) {
        System.out.println(Utility.getJson(createTaskResponse(1)));
    }
}
