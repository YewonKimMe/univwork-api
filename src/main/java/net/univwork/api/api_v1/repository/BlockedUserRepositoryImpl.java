package net.univwork.api.api_v1.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.BlockedUser;
import net.univwork.api.api_v1.repository.jpa.JpaBlockedUserRepository;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class BlockedUserRepositoryImpl implements BlockedUserRepository {

    private final JpaBlockedUserRepository jpaBlockedUserRepository;

    /**
     * 차단된 유저를 찾는 메소드
     * @since 1.0.0
     * @param blockedUser 차단 유저 cookie 문자열
     * @return 차단 유저 객체
     * */
    @Override
    public BlockedUser findBlockedUser(String blockedUser) {
        return jpaBlockedUserRepository.findBlockedUserByBlockedUser(blockedUser);
    }

    /**
     * 차단된 유저를 차단 해제 하는 메소드
     * @param uuid 유저 uuid 문자열
     * @since 1.0.0
     * @apiNote 어드민 전용
     * */
    @Override
    public void release(String uuid) {
        jpaBlockedUserRepository.releaseBlockedUser(uuid);
    }

    @Override
    public void blockAnonymousUser(BlockedUser anonymousBlockedUser) {
        jpaBlockedUserRepository.save(anonymousBlockedUser);
    }

    @Override
    public void blockUser(BlockedUser blockedUser) {
        jpaBlockedUserRepository.save(blockedUser);
    }
}
