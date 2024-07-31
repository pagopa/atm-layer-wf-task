package it.pagopa.atmlayer.wf.task.test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import io.smallrye.mutiny.Uni;
import it.pagopa.atmlayer.wf.task.bean.Device;
import it.pagopa.atmlayer.wf.task.bean.Peripheral;
import it.pagopa.atmlayer.wf.task.bean.State;
import it.pagopa.atmlayer.wf.task.bean.enumartive.Channel;
import it.pagopa.atmlayer.wf.task.bean.enumartive.Command;
import it.pagopa.atmlayer.wf.task.bean.enumartive.EppMode;
import it.pagopa.atmlayer.wf.task.bean.enumartive.PeripheralStatus;
import it.pagopa.atmlayer.wf.task.client.bean.GetTokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.PublicKey;
import it.pagopa.atmlayer.wf.task.client.bean.Task;
import it.pagopa.atmlayer.wf.task.client.bean.TaskResponse;
import it.pagopa.atmlayer.wf.task.client.bean.Token;
import it.pagopa.atmlayer.wf.task.client.bean.VariableResponse;
import it.pagopa.atmlayer.wf.task.test.bean.Dato;
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
        variableResponse.getVariables().put(Constants.FUNCTION_ID_CONTEXT_LOG, "FUNCTION_ID");
        variableResponse.getVariables().put(Constants.COMMAND_VARIABLE_VALUE, Command.PRINT_RECEIPT.name());
        ArrayList<Object> list = new ArrayList<>();
        Dato elemento = new Dato();
        elemento.setParagrafo("vedi2");
        elemento.setCircuits(new ArrayList<String>());
        elemento.getCircuits().add("VISA");
        elemento.getCircuits().add("MASTERCARD");
        list.add(elemento);
        
        
        variableResponse.getVariables().put("pulsanti", list);
        

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
        variableResponse.getVariables().put("externalComm", true);
        ArrayList<Object> list = new ArrayList<Object>();
        Dato elemento = new Dato();
        elemento.setParagrafo("vedi2");
        elemento.setCircuits(new ArrayList<String>());
        elemento.getCircuits().add("VISA");
        elemento.getCircuits().add("MASTERCARD");
        list.add(elemento);

        variableResponse.getVariables().put("pulsanti", list);

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
        Device deviceInfo = Device.builder().bankId("00001").branchId("0002").channel(Channel.ATM).code("1234").terminalId("1234567890").opTimestamp(new Date()).peripherals(perList).build();
        State state = new State();
        state.setDevice(deviceInfo);
        Map<String, Object> variablesData = new HashMap<>();
        variablesData.put("var1", "test");
        state.setData(variablesData);
        state.setFiscalCode("RSSMRA74D22A001Q");

        return state;
    }

    public static State createStateRequestStartWithoutDeviceData() {
        List<Peripheral> perList = new ArrayList<>();
        Peripheral per = new Peripheral();
        per.setId("PRINTER");
        per.setName("PRINTER");
        per.setStatus(PeripheralStatus.OK);
        perList.add(per);
        Device deviceInfo = Device.builder().bankId("00001").channel(Channel.ATM).opTimestamp(new Date()).peripherals(perList).build();
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
        varResponse.getVariables().put(Constants.RECEIPT_TEMPLATE, "riepilogoCommissioni.html");
        varResponse.getVariables().put(Constants.TEMPLATE_TYPE, "INFO");
        varResponse.getVariables().put("company", "Auriga");
        varResponse.getVariables().put("description", "descrizione");
        varResponse.getVariables().put("amount", 10000);
        varResponse.getVariables().put("fee", 2.30);
        varResponse.getVariables().put("totale", 11.50);
        ArrayList<Object> list = new ArrayList<>();

        Dato elemento = new Dato();
        elemento.setParagrafo("vedi2");
        elemento.setCircuits(new ArrayList<String>());
        elemento.getCircuits().add("VISA");
        elemento.getCircuits().add("MASTERCARD");
        list.add(elemento);

        varResponse.getVariables().put("pulsanti", list);

        return varResponse;
    }

    public static VariableResponse createvaVariableResponseMissingReceipt() {
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

    public static PublicKey createPublicKeyResponse() {
        PublicKey key = new PublicKey();

        key.setKty("RSA");
        key.setE(Base64.getDecoder().decode("AQAB"));
        key.setUse("enc");
        key.setKid("d0d654e697da4848b56899fedccb642b/4536def850ac6e9830f");
        key.setExp(1678975089);
        key.setIat(1678888689);
        key.setModulus(Base64.getDecoder().decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3iu1kH1foan71+X13MQ6WIRhuTw70zhtXxC5UyHGmNcDabqqrzdKovlPDZt05VuktpP+di0ZtKnwjRxzx2IUwO2s05kT8qI+acfEf4IJR3J6yCrnYmSdVtdb+Oy5VkqbUn/xVLidOED2dfMgvCobfDdiLL1dqp7Ll8i+UUvcDTvQ/c2LwSqHT5vY8n5mXWPRzHundNG8572AqI6DNQSCo3rRFtgP4vwbsYZX5+4o/Jvk4qrBALkfbq1RGmM6kVGokEG53yjlmAuDb2OEOeqYtQxFUulcVYRMZZY5ruuuOst77+U72hT1YHXA/gJexDVsetZnfzgMQUZABw+1ZjFjTwIDAQAB"));

        return key;
    }

    public static GetTokenResponse createGetTokenResponse() {
        return GetTokenResponse.builder().token("TOKEN").build();
    }

    public static Uni<Token> getTokenResponse() {
    	Token token = new Token();
        token.setAccessToken("test");
        return Uni.createFrom().voidItem().replaceWith(token);
    }
    
    public static void main(String[] args) {
        System.out.println(Utility.getJson(createPublicKeyResponse()));
    }
}
