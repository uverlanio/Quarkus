package io.github.uverlaniomps.quarkussocial.rest;

import io.github.uverlaniomps.quarkussocial.domain.model.Follower;
import io.github.uverlaniomps.quarkussocial.domain.model.User;
import io.github.uverlaniomps.quarkussocial.domain.repository.FollowerRepository;
import io.github.uverlaniomps.quarkussocial.domain.repository.UserRepository;
import io.github.uverlaniomps.quarkussocial.rest.dto.FollowerRequest;
import io.github.uverlaniomps.quarkussocial.rest.dto.FollowerResponse;
import io.github.uverlaniomps.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }
    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request){
        User user = userRepository.findById(userId);

        if(userId.equals(request.getFollowerId())){
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        User follower = userRepository.findById(request.getFollowerId());

        // verificar se o seguidor segue o usuário
        boolean follows = followerRepository.follows(follower, user);

        if(!follows){
            Follower entity = Follower
                    .builder()
                    .user(user)
                    .follower(follower)
                    .build();

            followerRepository.persist(entity);
        }

        return Response.noContent().build();
    }
    @GET
    public Response listFollowers(@PathParam("userId") Long userId){
        User user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = followerRepository.findByUser(userId);

        FollowersPerUserResponse fpur = new FollowersPerUserResponse();
        fpur.setFollowersCount(list.size());

        var followeList = list.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        fpur.setContent(followeList);

        return Response.ok(fpur).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(
            @PathParam("userId") Long userId,
            @QueryParam("followerId") Long followerId){
        User user = userRepository.findById(userId);

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
