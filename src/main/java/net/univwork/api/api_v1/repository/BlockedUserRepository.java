package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.entity.BlockedUser;

public interface BlockedUserRepository {

    /**
     * 차단된 유저를 찾는 메소드
     * @since 1.0.0
     * @param blockedUser 차단 유저 cookie 문자열
     * @return 차단 유저 객체
     * */
    BlockedUser findBlockedUser(String blockedUser);

    /**
     * 차단된 유저를 차단 해제 하는 메소드
     * @param uuid 유저 uuid 문자열
     * @since 1.0.0
     * */
    void release(String uuid);

    void blockAnonymousUser(BlockedUser anonymousBlockedUser);

    void blockUser(BlockedUser user);
}
