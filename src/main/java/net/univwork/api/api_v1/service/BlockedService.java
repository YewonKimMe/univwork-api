package net.univwork.api.api_v1.service;

import net.univwork.api.api_v1.domain.entity.BlockedIp;
import net.univwork.api.api_v1.domain.entity.BlockedUser;

public interface BlockedService {

    /**
     * 차단된 유저 검색
     * @param blockedUserId 유저 아이디 문자열
     * @return 차단 유저 객체
     * @since 1.0.0
     * */
    BlockedUser findBlockedUser(String blockedUserId);

    /**
     * 차단된 아이피 검색
     * @param blockedIp 차단 아이피 문자열
     * @return 차단 아이피 객체
     * @since 1.0.0
     * */
    BlockedIp findBlockedIp(String blockedIp);

    /**
     * 차단된 유저를 차단 해제 하는 메소드
     * @param blockedUserId 유저 uuid 문자열
     * @since 1.0.0
     * @apiNote 어드민 전용
     * */
    void releaseUser(String blockedUserId);

    /**
     * 차단된 아이피를 차단 해제 하는 메소드
     * @param blockedIp 아이피 문자열
     * @apiNote 어드민 전용
     * @since 1.0.0
     * */
    void releaseIp(String blockedIp);

    void blockAnonymousUser(String userId, String reason);

    void blockUser(String userId, String reason);

    void blockIp(String userIp, String reason);
}
