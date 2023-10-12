package it.pagopa.atmlayer.wf.task.test;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class TaskResourceTest {

    @Test
    public void testHelloEndpoint() {
        given().contentType(ContentType.JSON).body(
                "{\"DeviceInfo\":{\"bankId\":\"0001\",\"deviceId\":\"1234\",\"timestamp\":\"2023-10-06T14:30:00Z\"}}")
                .when().post("/api/v2/tasks/nextSteps")
                .then()
                .statusCode(200);
    }

}