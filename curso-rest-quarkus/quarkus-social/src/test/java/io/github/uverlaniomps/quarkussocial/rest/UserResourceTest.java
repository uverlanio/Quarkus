package io.github.uverlaniomps.quarkussocial.rest;

import com.sun.tools.rngom.util.Uri;
import io.github.uverlaniomps.quarkussocial.domain.model.User;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.json.bind.JsonbBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Should create a user successfully")
    @Order(1)
    void createUserTest() {

        var user = new User();
        user.setName("Fulano");
        user.setAge(35);

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .body(JsonbBuilder.create().toJson(user))
                .when()
                    .post(apiURL.toString())
                .then()
                    .extract().response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id")); // verificando o id do json

    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    void createUserValidationErrorTest() {

        var user = new User();
        user.setName(null);
        user.setAge(null);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(JsonbBuilder.create().toJson(user))
                        .when()
                        .post(apiURL.toString())
                        .then()
                        .extract().response();

        assertEquals(422, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
    }

    @Test
    @Order(3)
    void listUsersTest() {

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(apiURL.toString())
                .then()
                .body("size()", Matchers.is(1));
    }


}