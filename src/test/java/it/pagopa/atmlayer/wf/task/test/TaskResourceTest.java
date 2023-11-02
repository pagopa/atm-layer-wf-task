package it.pagopa.atmlayer.wf.task.test;

import static io.restassured.RestAssured.given;

import java.util.UUID;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
public class TaskResourceTest {

        @InjectMock
        @RestClient
        @MockitoConfig(convertScopes = true)
        ProcessRestClient processRestClient;

        @Test
        public void startProcessOk() {

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
        public void variableResponseWithData() {

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
        public void nextTaskOk() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponse(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", UUID.randomUUID().toString())
                                .then().extract().response();

                Assertions.assertEquals(201, response.statusCode());
        }

        @Test
        public void templateNotFound() {

                Mockito.when(processRestClient.nextTasks(Mockito.any(TaskRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createTaskResponseMissingHtml(1)));

                Mockito.when(processRestClient
                                .retrieveVariables(Mockito.any(VariableRequest.class)))
                                .thenReturn(RestResponse.status(Status.OK,
                                                DataTest.createVariableResponseNoData()));

                Response response = given().body(DataTest.createStateRequestNext())
                                .contentType(MediaType.APPLICATION_JSON).when()
                                .post("/next/trns/{transactionId}", UUID.randomUUID().toString())
                                .then().extract().response();

                Assertions.assertEquals(500, response.statusCode());
        }

}
