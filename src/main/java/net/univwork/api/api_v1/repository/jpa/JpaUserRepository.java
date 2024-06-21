package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
}
