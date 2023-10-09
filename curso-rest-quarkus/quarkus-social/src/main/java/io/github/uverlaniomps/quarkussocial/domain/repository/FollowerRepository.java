package io.github.uverlaniomps.quarkussocial.domain.repository;

import io.github.uverlaniomps.quarkussocial.domain.model.Follower;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {
}
