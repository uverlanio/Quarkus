package io.github.uverlaniomps.quarkussocial.rest;

import io.github.uverlaniomps.quarkussocial.domain.model.Follower;
import io.github.uverlaniomps.quarkussocial.domain.model.User;
import io.github.uverlaniomps.quarkussocial.domain.repository.FollowerRepository;
import io.github.uverlaniomps.quarkussocial.domain.repository.UserRepository;
import io.github.uverlaniomps.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        // Usuário padrão de testes
        User user = User.builder()
                .name("Fulano")
                .age(30)
                .build();

        userRepository.persist(user);
        userId = user.getId();

        User follower = User.builder()
                .name("Cicrano")
                .age(31)
                .build();

        userRepository.persist(follower);
        followerId = follower.getId();

        Follower followerEntity = new Follower();
        followerEntity.setUser(user);
        followerEntity.setFollower(follower);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to User id")
    void sameUserAsFollowerTest(){

        FollowerRequest request = new FollowerRequest();
        request.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(JsonbBuilder.create().toJson(request))
                .when()
                .put()
                .then()
                .statusCode(409)
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("Should return 404 when followerId is equal to User doesn't exist")
    void userNotFoundWhenTryingFollowTest(){

        FollowerRequest request = new FollowerRequest();
        request.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 999)
                .body(JsonbBuilder.create().toJson(request))
                .when()
                .put()
                .then()
                .statusCode(404);
    }
    @Test
    @DisplayName("Should follow a user")
    void followUserTest(){

        FollowerRequest request = new FollowerRequest();
        request.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(JsonbBuilder.create().toJson(request))
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and User id doesn't exist")
    void userNotFoundWhenListingFollowersTest(){

        FollowerRequest request = new FollowerRequest();
        request.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 999)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should list a user's followers")
    void listFollowersTest(){

            var response =  given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .extract().response();

            var followersCount = response.jsonPath().get("followersCount");
            var followersContent = response.jsonPath().getList("content");
            assertEquals(200, response.getStatusCode());
            assertEquals(1, followersCount);
            assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("Should return 404 on unfollow user and User id doesn't exist")
    void userNotFoundWhenUnfollowingUserTest(){

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", 999)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should unfollow an use")
    void unfollowUserTest(){

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}