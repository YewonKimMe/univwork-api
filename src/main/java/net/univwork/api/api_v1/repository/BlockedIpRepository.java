package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.entity.BlockedIp;

public interface BlockedIpRepository {

    /**
     * ip String 으로 차단된 아이피 찾는 메소드
     * @param blockedIp 아이피 문자열
     * @return 차단된 아이피 객체
     * @since 1.0.0
     * */
    BlockedIp findBlockedIp(String blockedIp);

    /**
     * 차단된 아이피를 차단 해제 하는 메소드
     * @param blockedIp 아이피 문자열
     * @apiNote 어드민 전용
     * @since 1.0.0
     * */
    void release(String blockedIp);
}
