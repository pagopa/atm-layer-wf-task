package it.pagopa.atmlayer.wf.task.test;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class TaskResourceTest {

    private String test = "{\"device\":{\"bank_id\":\"02008\",\"branch_id\":\"12345\",\"code\":\"0001\",\"terminal_id\":\"ABCD1234\",\"channel\":\"ATM\",\"peripherals\":[{\"id\":\"PRINTER\",\"name\":\"Receipt printer\",\"status\":\"OK\"}],\"opTimestamp\":\"2022-03-10T16:15:50Z\"},\"task_id\":\"string\",\"data\":{\"additionalProp1\":\"string\",\"additionalProp2\":\"string\",\"additionalProp3\":\"string\"}}";

    @Test
    public void testHelloEndpoint() {
        given().contentType(ContentType.JSON).body(test)
                .when().post("/api/v1/tasks/main/123/trns/1234")
                .then()
                .statusCode(201);
    }

}