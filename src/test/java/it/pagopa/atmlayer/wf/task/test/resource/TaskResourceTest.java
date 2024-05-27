package it.pagopa.atmlayer.wf.task.test.resource;

import static io.restassured.RestAssured.given;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.MockitoConfig;
import io.restassured.response.Response;
import it.pagopa.atmlayer.wf.task.client.MilAuthRestClient;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.TokenizationRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.resource.TaskResource;
import it.pagopa.atmlayer.wf.task.test.DataTest;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
@TestHTTPEndpoint(TaskResource.class)
@TestInstance(Lifecycle.PER_CLASS)
class TaskResourceTest {

        @InjectMock
        @RestClient
        @MockitoConfig(convertScopes = true)
        MilAuthRestClient milAuthRestClient;

        @InjectMock
        @RestClient
        @MockitoConfig(convertScopes = true)
        ProcessRestClient processRestClient;

        @InjectMock
        @RestClient
        @MockitoConfig(convertScopes = true)
        TokenizationRestClient tokenizationRestClient;
        

        @Test
        void startProcessOk() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();
                
                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessOkWithPan() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Mockito.when(tokenizationRestClient
                                .getKey()).thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createPublicKeyResponse()));

                Response response = given().body(
                                "{\"device\":{\"bankId\":\"00001\",\"branchId\":\"0002\",\"code\":\"1234\",\"terminalId\":\"1234567890\",\"opTimestamp\":1707323349628,\"channel\":\"ATM\",\"peripherals\":[{\"id\":\"PRINTER\",\"name\":\"PRINTER\",\"status\":\"OK\"}]},\"data\":{\"var1\":\"test\"},\"panInfo\":[{\"pan\":\"1234567891234567\",\"circuits\":[\"VISA\",\"MASTERCARD\"],\"bankName\":\"ISYBANK\"}]}")
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        // @Test
        // void startProcessOkWithPanGenerateKey() {
        //
        // Mockito.when(milAuthRestClient.getToken(Mockito.anyString(),
        // Mockito.anyString(), Mockito.any(), Mockito.anyString(),
        // Mockito.anyString()))
        // .thenReturn(RestResponse.status(Status.OK, new
        // TokenResponse("****fiscalcode****")));
        //
        //
        // Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
        // .thenReturn(RestResponse.status(Status.OK,
        // DataTest.createTaskResponse(1)));
        //
        // Mockito.when(processRestClient
        // .retrieveVariables(Mockito.any(VariableRequest.class)))
        // .thenReturn(RestResponse.status(Status.OK,
        // DataTest.createVariableResponseNoData()));
        //
        // Mockito.when(prop.tokenizationIsMock()).thenReturn(false);
        //
        // Response response =
        // given().body("{\"device\":{\"bankId\":\"00001\",\"branchId\":\"0002\",\"code\":\"12345\",\"terminalId\":\"1234567890\",\"opTimestamp\":1707323349628,\"channel\":\"ATM\",\"peripherals\":[{\"id\":\"PRINTER\",\"name\":\"PRINTER\",\"status\":\"OK\"}]},\"data\":{\"var1\":\"test\"},\"panInfo\":[{\"pan\":\"1234567891234567\",\"circuits\":[\"VISA\",\"MASTERCARD\"],\"bankName\":\"ISYBANK\"}]}")
        // .contentType(MediaType.APPLICATION_JSON).when()
        // .post("/main").then().extract().response();
        //
        // Assertions.assertEquals(201, response.statusCode());
        // }

        @Test
        void startProcessOkWithoutDeviceData() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStartWithoutDeviceData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessConnectionProblem() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenThrow(new ProcessingException("error"));

                given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().statusCode(StatusCode.INTERNAL_SERVER_ERROR);

        }

        @Test
        void taskWithoutButtons() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoButtons()));


                Response response = given().body(DataTest.createStateRequestStartWithoutDeviceData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void taskWithoutForm() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseNoForm(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseWithData()));

                Response response = given().body(DataTest.createStateRequestStartWithoutDeviceData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                System.out.println(response.toString());
                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessKoOnStart500() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.INTERNAL_SERVER_ERROR));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

        @Test
        void startProcessKoOnStart503() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenThrow(new WebApplicationException(Status.SERVICE_UNAVAILABLE));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @Test
        void startProcessKoOnVariables500() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK, DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.INTERNAL_SERVER_ERROR));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

        @Test
        void startProcessKoOnVariables503() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK, DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.SERVICE_UNAVAILABLE));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @Test
        void variableResponseWithData() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseWithData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void nextTaskOk() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void nextTaskConnectionProblem() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenThrow(new ProcessingException("error"));

                given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa")
                                .then()
                                .statusCode(StatusCode.INTERNAL_SERVER_ERROR);

        }

        @Test
        void nextTaskKoOnNextTask500() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenThrow(new WebApplicationException(Status.INTERNAL_SERVER_ERROR));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

        @Test
        void nextTaskKoOnNextTask503() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenThrow(new WebApplicationException(Status.SERVICE_UNAVAILABLE));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @Test
        void nextTaskKoOnVariables500() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenThrow(new WebApplicationException(Status.INTERNAL_SERVER_ERROR));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

        @Test
        void nextTaskKoOnVariables503() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenThrow(new WebApplicationException(Status.SERVICE_UNAVAILABLE));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @Test
        void taskNoPeripherals() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNoPeripheral())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main")
                                .then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void templateNotFound() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseMissingHtml(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "10001-0002-1234-1234567890-aaaaaaaaaaaaa",
                        "00001-1002-1235-1234567890-aaaaaaaaaaaaa",
                        "00001-0002-1236-1234567890-aaaaaaaaaaaaa",
                        "00001-0002-1235-1234567891-aaaaaaaaaaaaa" })
        void wrongTransactionIdOnNext(String transactionId) {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", transactionId)
                                .then().extract().response();

                Assertions.assertEquals(400, response.statusCode());
        }

        @Test
        void noBranchIdTransactionIdOnNext() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNextNoBranchId())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001--1234-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void noCodeTransactionIdOnNext() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNextNoCode())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002--1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void noTerminalIdTransactionIdOnNext() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNextNoTerminalId())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234--aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessOkNoData() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNoData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessNoTasks() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseNoTasks()));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNoData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(200, response.statusCode());
        }

        @Test
        void testDefaultVariables() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createvaVariableResponseDefaultVariables()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void testMissingReceipt() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseMissingReceipt(1)));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createvaVariableResponseMissingReceipt()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @Test
        void testReturn202ProcessOnNext() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.ACCEPTED,
                                                DataTest.createTaskResponseEndProcess()));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createvaVariableResponseDefaultVariables()));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa").then()
                                .extract().response();

                Assertions.assertEquals(202, response.statusCode());
        }

        @Test
        void testReturn202ProcessOnStart() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.ACCEPTED,
                                                DataTest.createTaskResponseEndProcess()));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createvaVariableResponseDefaultVariables()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then()
                                .extract().response();

                Assertions.assertEquals(202, response.statusCode());
        }

        @Test
        void testNoVariablesRequest() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseNoVariablesRequest(1)));

                Mockito.when(processRestClient.retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createvaVariableResponseDefaultVariables()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa").then()
                                .extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void testEndProcess() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseEndProcess()));

                Mockito.when(milAuthRestClient.deleteToken(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.NO_CONTENT));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa").then()
                                .extract().response();

                Assertions.assertEquals(200, response.statusCode());
        }

        @Test
        void testEndProcessErrorDelete() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseEndProcess()));

                Mockito.when(milAuthRestClient.deleteToken(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString())).thenReturn((RestResponse.status(Status.NOT_FOUND)));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa").then()
                                .extract().response();

                Assertions.assertEquals(200, response.statusCode());
        }

        @Test
        void testEndProcessErrorDeleteUnknown() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseEndProcess()));

                Mockito.when(milAuthRestClient.deleteToken(Mockito.anyString(), Mockito.anyString(),
                                Mockito.anyString(), Mockito.anyString())).thenReturn(RestResponse.serverError());

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa").then()
                                .extract().response();

                Assertions.assertEquals(200, response.statusCode());
        }

        @Test
        void startProcessKoFromProcess500() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenThrow(new WebApplicationException(Status.INTERNAL_SERVER_ERROR));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

        @Test
        void startProcessKoFromProcess503() {

                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenThrow(new WebApplicationException(Status.SERVICE_UNAVAILABLE));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @Test
        void startProcessKoFromGetToken() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Status.SERVICE_UNAVAILABLE));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessKoFromGetTokenError() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Status.SERVICE_UNAVAILABLE));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.serverError());

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

        @Test
        void startProcessKoFromGetTokenNotFound() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenThrow(new WebApplicationException(Status.SERVICE_UNAVAILABLE));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.notFound());

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(209, response.statusCode());
        }

        @Test
        void startProcessKoForbiddenFromGetToken() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.FORBIDDEN));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessOKCFGetToken() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(
                                "{\"device\":{\"bankId\":\"00001\",\"branchId\":\"0002\",\"code\":\"1234\",\"terminalId\":\"1234567890\",\"opTimestamp\":1705499660669,\"channel\":\"ATM\",\"peripherals\":[{\"id\":\"PRINTER\",\"name\":\"PRINTER\",\"status\":\"OK\"}]},\"data\":{\"var1\":\"test\"},\"fiscalCode\":\"BBTFNC\"}")
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessOKMalformed() {
                Mockito.when(milAuthRestClient.getToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(),
                                Mockito.anyString(), Mockito.anyString()))
                                .thenReturn(RestResponse.status(Status.OK, new TokenResponse("****fiscalcode****")));

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(
                                "{\"devicee\":{\"bankId\":\"00001\",\"branchId\":\"0002\",\"code\":\"1234\",\"terminalId\":\"1234567890\",\"opTimestamp\":1705499660669,\"channel\":\"ATM\",\"peripherals\":[{\"id\":\"PRINTER\",\"name\":\"PRINTER\",\"status\":\"OK\"}]},\"data\":{\"var1\":\"test\"},\"fiscalCode\":\"BBTFNC\"}")
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main").then().extract().response();

                Assertions.assertEquals(400, response.statusCode());
                response = given().body(
                                "{\"devicee\":{\"bankId\":\"00001\",\"branchId\":\"0002\",\"code\":\"1234\",\"terminalId\":\"1234567890\",\"opTimestamp\":1705499660669,\"channel\":\"ATM\",\"peripherals\":[{\"id\":\"PRINTER\",\"name\":\"PRINTER\",\"status\":\"OK\"}]},\"data\":{\"var1\":\"test\"},\"fiscalCode\":\"BBTFNC\"}")
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-1234-1234567890-aaaaaaaaaaaaa").then()
                                .extract().response();

                Assertions.assertEquals(400, response.statusCode());
        }

}
