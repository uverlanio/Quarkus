package io.github.uverlaniomps.quarkussocial.rest.dto;

import io.github.uverlaniomps.quarkussocial.domain.model.Follower;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse(Follower follower){
        //this referencia o construtor @AllArgsConstructor
        this(follower.getId(), follower.getFollower().getName());
    }

}
