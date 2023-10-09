package io.github.uverlaniomps.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text")
    private String text;
    @Column
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "user_id") // Quando existir chave estrangeira
    private User user;

    @PrePersist // Com essa anotação, não se faz necessário preencher esse campo nos objetos
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }

}
