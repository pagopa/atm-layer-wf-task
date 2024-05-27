package it.pagopa.atmlayer.wf.task.test.resource;

import static io.restassured.RestAssured.given;

import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;

import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.MockitoConfig;
import io.restassured.response.Response;
import it.pagopa.atmlayer.wf.task.client.MilAuthRestClient;
import it.pagopa.atmlayer.wf.task.client.ProcessRestClient;
import it.pagopa.atmlayer.wf.task.client.TokenizationRestClient;
import it.pagopa.atmlayer.wf.task.client.bean.GetTokenRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TaskRequest;
import it.pagopa.atmlayer.wf.task.client.bean.TokenResponse;
import it.pagopa.atmlayer.wf.task.client.bean.VariableRequest;
import it.pagopa.atmlayer.wf.task.database.dynamo.service.ConfigurationAsyncServiceImpl;
import it.pagopa.atmlayer.wf.task.logging.sensitive.SensitiveDataTracer;
import it.pagopa.atmlayer.wf.task.resource.TaskResource;
import it.pagopa.atmlayer.wf.task.test.DataTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
@TestHTTPEndpoint(TaskResource.class)
@TestInstance(Lifecycle.PER_CLASS)
@TestProfile(TaskResourceOverrideConfigTest.BuildTimeValueChangeTestProfile.class)
class TaskResourceOverrideConfigTest {

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

    @InjectMock
    ConfigurationAsyncServiceImpl ConfigurationAsyncServiceImpl;

    public static class BuildTimeValueChangeTestProfile implements QuarkusTestProfile {

        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("wf-task.config.tokenization-is-mock", "false");
        }
    }

    @Test
    void test() {
        SensitiveDataTracer.setIsTraceLoggingEnabled(true);

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

        Mockito.when(tokenizationRestClient
                .getToken(Mockito.any(GetTokenRequest.class))).thenReturn(RestResponse.status(Status.OK,
                        DataTest.createGetTokenResponse()));

        SensitiveDataTracer.tracerJob();

        Response response = given().body(
                "{\"device\":{\"bankId\":\"00001\",\"branchId\":\"0002\",\"code\":\"1234\",\"terminalId\":\"1234567890\",\"opTimestamp\":1707323349628,\"channel\":\"ATM\",\"peripherals\":[{\"id\":\"PRINTER\",\"name\":\"PRINTER\",\"status\":\"OK\"}]},\"data\":{\"var1\":\"test\"},\"panInfo\":[{\"pan\":\"1234567891234567\",\"circuits\":[\"VISA\",\"MASTERCARD\"],\"bankName\":\"ISYBANK\"}]}")
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/main")
                .then().extract().response();

        Assertions.assertEquals(201, response.statusCode());
    }
}
