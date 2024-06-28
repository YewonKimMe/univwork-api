package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUserId(String userId);

    int updatePassword(String userId, String newPassword);

    void withdraw(String userId);
}
