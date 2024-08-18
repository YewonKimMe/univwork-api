package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUserId(String userId);

    int updatePassword(String userId, String newPassword);

    int withdraw(String userId);

    Page<CommentDto> getCommentsPerUser(Pageable pageable, String username);
}
