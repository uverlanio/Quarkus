package io.github.uverlaniomps.quarkussocial.rest;

import io.github.uverlaniomps.quarkussocial.domain.model.User;
import io.github.uverlaniomps.quarkussocial.domain.repository.UserRepository;
import io.github.uverlaniomps.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;

    Long userId;
    @BeforeEach
    @Transactional
    void setUp(){
        User user = User.builder()
                .name("Fulano")
                .age(30)
                .build();

        userRepository.persist(user);
        userId = user.getId();
    }
    @Test
    void savePostTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        var response = given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(postRequest))
                .pathParam("userId", userId)
                .when()
                .post()
                .then()
                   .extract().response();

        assertEquals(201, response.getStatusCode());
    }
}