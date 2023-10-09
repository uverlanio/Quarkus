package io.github.uverlaniomps.quarkussocial.rest.dto;

import io.github.uverlaniomps.quarkussocial.domain.model.Post;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {

    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        return PostResponse.builder()
                .text(post.getText())
                .dateTime(post.getDateTime())
                .build();

    }

}
