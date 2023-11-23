package it.pagopa.atmlayer.wf.task.test.utility;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import it.pagopa.atmlayer.wf.task.resource.TaskResource;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestHTTPEndpoint(TaskResource.class)
class LogFilterTest {

    @Test
    void testLogFilter() {
        Response response = given().contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/health/v1/task")
                .then().extract().response();
        assertEquals(404, response.getStatusCode());

    }

    @Test
    void testLogFilterNoPathParams() {
        Response response = given().contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v1/tasks/main/")
                .then().extract().response();

        assertEquals(404, response.getStatusCode());
    }

}
