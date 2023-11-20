package it.pagopa.atmlayer.wf.task.test.utility;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jboss.resteasy.reactive.RestResponse.StatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogFilterTest {

    @Test
    void testLogFilter() {
        Response response = given().contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/api")
                .then()
                .statusCode(StatusCode.NOT_FOUND).extract().response();
        assertEquals(404, response.getStatusCode());

    }

    @Test
    void testLogFilterNoPathParams() {
        Response response = given().contentType(MediaType.APPLICATION_JSON)
                .when()
                .post("/api/v1/tasks/main/")
                .then()
                .statusCode(StatusCode.NOT_FOUND).extract().response();

        assertEquals(404, response.getStatusCode());
    }

}
