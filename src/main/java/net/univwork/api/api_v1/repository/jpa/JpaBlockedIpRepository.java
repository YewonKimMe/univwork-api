package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.BlockedIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaBlockedIpRepository extends JpaRepository<BlockedIp, Long> {

    /**
     * ip String 으로 차단된 아이피 찾는 메소드
     * @param blockedIp 아이피 문자열
     * @return 차단된 아이피 객체
     * @since 1.0.0
     * */
    BlockedIp findBlockedIpByBlockedIp(String blockedIp);

    /**
     * 차단된 아이피를 차단 해제 하는 메소드
     * @param ip 아이피 문자열
     * @since 1.0.0
     * */
    @Modifying
    @Query("DELETE FROM BlockedIp bi WHERE bi.blockedIp = :ip")
    void releaseBlockedIp(@Param("ip") String ip);

}
