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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
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
    @DisplayName("Should create a post for user")
    void savePostTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(postRequest))
                .pathParam("userId", userId)
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 when trying to make a post for an inexistent user")
    void postForAnInexistentUserTest(){

        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        Long inexistentUserId = 999L;

        var response = given()
                .contentType(ContentType.JSON)
                .body(JsonbBuilder.create().toJson(postRequest))
                .pathParam("userId", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(400);

    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    void listPostUserNotFoundTest(){
        var inexistentUserId = 999;

        given()
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(400);

    }

    @Test
    @DisplayName("Should return 400 when followerId header is not present")
    void listPostFollowerHeaderNotSendTest(){

        given()
                .pathParam("userId", userId)
                .headers("followerId", 1L)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when follower doesn't exist")
    void listPostFollowerNotFoundTest(){
        Long inexistentFollowerId = 999L;

        given()
                .pathParam("userId", userId)
                .headers("followerId", inexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("Should return 403 when follower isn't a follower")
    void listPostNotAFollowerTest(){

    }

    @Test
    @DisplayName("Should return posts")
    void listPostTest(){

    }
}