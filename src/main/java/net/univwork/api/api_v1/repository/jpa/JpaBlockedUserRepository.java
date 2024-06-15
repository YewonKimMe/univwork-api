package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaBlockedUserRepository extends JpaRepository<BlockedUser, Long> {

    /**
     * 차단된 유저를 찾는 메소드
     * @since 1.0.0
     * @param blockedUser 차단 유저 cookie 문자열
     * @return 차단 유저 객체
     * */
    BlockedUser findBlockedUserByBlockedUser(String blockedUser);

    /**
     * 차단된 유저를 차단 해제 하는 메소드
     * @param uuid 유저 uuid 문자열
     * @since 1.0.0
     * */
    @Modifying
    @Query("DELETE FROM BlockedUser bu WHERE bu.blockedUser = :uuid")
    void releaseBlockedUser(@Param("uuid") String uuid);

}
