package net.univwork.api.api_v1.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.BlockedIp;
import net.univwork.api.api_v1.repository.jpa.JpaBlockedIpRepository;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BlockedIpRepositoryImpl implements BlockedIpRepository {

    private final JpaBlockedIpRepository jpaBlockedIpRepository;

    /**
     * ip String 으로 차단된 아이피 찾는 메소드
     * @param blockedIp 아이피 문자열
     * @return 차단된 아이피 객체
     * @since 1.0.0
     * */
    @Override
    public BlockedIp findBlockedIp(String blockedIp) {
        return jpaBlockedIpRepository.findBlockedIpByBlockedIp(blockedIp);
    }

    /**
     * 차단된 아이피를 차단 해제 하는 메소드
     * @param blockedIp 아이피 문자열
     * @apiNote 어드민 전용
     * @since 1.0.0
     * */
    @Override
    public void release(String blockedIp) {
        jpaBlockedIpRepository.releaseBlockedIp(blockedIp);
    }
}
