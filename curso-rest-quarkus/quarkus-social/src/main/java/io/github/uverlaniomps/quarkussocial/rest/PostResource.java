package io.github.uverlaniomps.quarkussocial.rest;

import io.github.uverlaniomps.quarkussocial.domain.model.Post;
import io.github.uverlaniomps.quarkussocial.domain.model.User;
import io.github.uverlaniomps.quarkussocial.domain.repository.FollowerRepository;
import io.github.uverlaniomps.quarkussocial.domain.repository.PostRepository;
import io.github.uverlaniomps.quarkussocial.domain.repository.UserRepository;
import io.github.uverlaniomps.quarkussocial.rest.dto.CreatePostRequest;
import io.github.uverlaniomps.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    private UserRepository userRepository;
    @Inject
    private PostRepository postRepository;
    @Inject
    private FollowerRepository followerRepository;

    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(
            @PathParam("userId") Long userId, CreatePostRequest postRequest){
        User user = userRepository.findById(userId);

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = Post.builder()
                .user(user)
                .text(postRequest.getText())
                .build();

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(
            @PathParam("userId") Long userId,
            @HeaderParam("followerId") Long followerId){

        User user = userRepository.findById(userId);

        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You forgot the header followerId")
                    .build();
        }

        User follower = userRepository.findById(followerId);

        if(follower == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Inexistent followerId")
                    .build();
        }

        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("You can't see these followers")
                    .build();
        }

        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        List<PostResponse> postResponse = query.stream()
                //.map(post -> PostResponse.fromEntity(post))
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponse).build();
    }
}
