package it.pagopa.atmlayer.wf.task.test;

import static io.restassured.RestAssured.given;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
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
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.resource.TaskResource;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
@TestHTTPEndpoint(TaskResource.class)
@TestInstance(Lifecycle.PER_CLASS)
class TaskResourceTest {

        @InjectMock
        @RestClient
        @MockitoConfig(convertScopes = true)
        ProcessRestClient processRestClient;

        @Test
        void startProcessOk() {

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main/{functionId}", "demo23").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessOkWithoutDeviceData() {

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStartWithoutDeviceData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main/{functionId}", "demo23").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void taskWithoutButtons() {

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoButtons()));

                Response response = given().body(DataTest.createStateRequestStartWithoutDeviceData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main/{functionId}", "demo23").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void taskWithoutForm() {

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseNoForm(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseWithData()));

                Response response = given().body(DataTest.createStateRequestStartWithoutDeviceData())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main/{functionId}", "demo23").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void startProcessKoOnStart() {

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.INTERNAL_SERVER_ERROR));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main/{functionId}", "demo23").then().extract().response();

                Assertions.assertEquals(502, response.statusCode());
        }

        @Test
        void startProcessKoOnVariables() {

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK, DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.INTERNAL_SERVER_ERROR));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main/{functionId}", "demo23").then().extract().response();

                Assertions.assertEquals(502, response.statusCode());
        }

        @Test
        void variableResponseWithData() {

                Mockito.when(processRestClient.startProcess(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseWithData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/main/{functionId}", "demo23").then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        void nextTaskOk() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-12345-1234567890-aaaaaaaaaaaaa")
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
                                .post("/next/trns/{transactionId}", "00001-0002-12345-1234567890-aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

        @Test
        void noTaskIdOnNext() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestStart())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-12345-1234567890-aaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(400, response.statusCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "10001-0002-12345-1234567890-aaaaaaaaaaaaa",
                        "00001-1002-12345-1234567890-aaaaaaaaaaaaa",
                        "00001-0002-12346-1234567890-aaaaaaaaaaaaa",
                        "00001-0002-12345-1234567891-aaaaaaaaaaaaa" })
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
                                                DataTest.createVariableResponseWithData()));

                Response response = given().body(DataTest.createStateRequestNextNoBranchId())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001--12345-1234567890-aaaaaaaaaaaaa")
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
                                                DataTest.createVariableResponseWithData()));

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
                                                DataTest.createVariableResponseWithData()));

                Response response = given().body(DataTest.createStateRequestNextNoTerminalId())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", "00001-0002-12345--aaaaaaaaaaaaa")
                                .then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

}
