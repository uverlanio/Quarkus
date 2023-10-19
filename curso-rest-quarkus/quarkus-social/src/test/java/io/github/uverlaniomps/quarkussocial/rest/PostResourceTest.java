package io.github.uverlaniomps.quarkussocial.rest;

import io.github.uverlaniomps.quarkussocial.domain.model.Follower;
import io.github.uverlaniomps.quarkussocial.domain.model.Post;
import io.github.uverlaniomps.quarkussocial.domain.model.User;
import io.github.uverlaniomps.quarkussocial.domain.repository.FollowerRepository;
import io.github.uverlaniomps.quarkussocial.domain.repository.PostRepository;
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

    @Inject
    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;
    @BeforeEach
    @Transactional
    void setUp(){
        // Usuário padrão de testes
        User user = User.builder()
                .name("Fulano")
                .age(30)
                .build();

        userRepository.persist(user);
        userId = user.getId();

        // Criar a postagem do usuário
        Post post = Post.builder()
                .text("Hello")
                .user(user)
                .build();

        postRepository.persist(post);

        User userNotFollower = User.builder()
                .name("Cicrano")
                .age(33)
                .build();

        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        User userFollower = User.builder()
                .name("Terceiro")
                .age(31)
                .build();

        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = Follower.builder()
                .user(user)
                .follower(userFollower)
                .build();

        followerRepository.persist(follower);
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
                .statusCode(404);

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
        given()
                .pathParam("userId", userId)
                .headers("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these followers"));
    }

    @Test
    @DisplayName("Should return posts")
    void listPostTest(){
        given()
                .pathParam("userId", userId)
                .headers("followerId", userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));

    }
}