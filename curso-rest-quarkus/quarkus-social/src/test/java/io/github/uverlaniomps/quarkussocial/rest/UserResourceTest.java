package io.github.uverlaniomps.quarkussocial.rest;

import groovy.json.JsonBuilder;
import io.github.uverlaniomps.quarkussocial.domain.model.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UserResourceTest {

    @Test
    @DisplayName("Should create a user successfully")
    void createUserTest() {

        var user = new User();
        user.setName("Fulano");
        user.setAge(35);

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(JsonbBuilder.create().toJson(user))
                .when()
                    .post("/users")
                .then()
                    .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id")); // verificando o id do json

    }

    @Test
    @DisplayName("Should return error when json is not valid")
    void createuserValidationErrorTest() {

        var user = new User();
        user.setName(null);
        user.setAge(null);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(JsonbBuilder.create().toJson(user))
                        .when()
                        .post("/users")
                        .then()
                        .extract().response();

        assertEquals(422, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
    }

    @Test
    void deleteUsers() {
    }

    @Test
    void updateUsers() {
    }
}