package net.univwork.api.api_v1.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findUserByEmail(final String email) {
        return jpaUserRepository.findUserByEmail(email);
    }

    @Override
    public Optional<User> findUserByUserId(final String userId) {
        return jpaUserRepository.findUserByUserId(userId);
    }

    @Override
    public int updatePassword(final String userId, final String hashedPw) {
        return jpaUserRepository.updatePwd(userId, hashedPw);
    }

    @Override
    public void withdraw(final String userId) {
        jpaUserRepository.deleteUserByUserId(userId);
    }
}
