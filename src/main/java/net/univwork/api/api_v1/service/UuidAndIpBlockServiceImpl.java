package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import net.univwork.api.api_v1.domain.entity.BlockedIp;
import net.univwork.api.api_v1.domain.entity.BlockedUser;
import net.univwork.api.api_v1.repository.BlockedIpRepository;
import net.univwork.api.api_v1.repository.BlockedUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UuidAndIpBlockServiceImpl implements BlockedService {

    private final BlockedUserRepository userRepository;

    private final BlockedIpRepository ipRepository;

    @Override
    public BlockedUser findBlockedUser(String blockedUserId) {
        return userRepository.findBlockedUser(blockedUserId);
    }

    @Override
    public BlockedIp findBlockedIp(String blockedIp) {
        return ipRepository.findBlockedIp(blockedIp);
    }

    @Override
    public void releaseUser(String blockedUserId) {
        userRepository.release(blockedUserId);
    }

    @Override
    public void releaseIp(String blockedIp) {
        ipRepository.release(blockedIp);
    }
}
