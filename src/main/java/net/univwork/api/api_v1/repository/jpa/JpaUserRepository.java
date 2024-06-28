package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 유저 조회
     * */
    Optional<User> findUserByEmail(String email);

    /**
     * 유저 아이디로 유저 조회
     * */
    Optional<User> findUserByUserId(String userId);

    /**
     * 유저 작성 댓글 조회
     * */

    /**
     * 비밀번호 변경
     * */
    @Modifying
    @Query("UPDATE User u SET u.pwd = :newPassword WHERE u.userId = :userId")
    int updatePwd(@Param("userId") String userId, @Param("newPassword") String newPassword);

    /**
     * 탈퇴
     */
    int deleteUserByUserId(String userId);
}
