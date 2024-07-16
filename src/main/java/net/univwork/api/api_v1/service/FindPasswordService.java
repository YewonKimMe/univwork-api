package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.exception.UserNotExistException;
import net.univwork.api.api_v1.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class FindPasswordService {

    private final RedisService redisService;

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final String message = "해당 이메일로 검색된 계정이 없습니다.";

    // auth token 발행 후 이메일 발송
    public void sendFindPasswordEmail(String email) {

        // 이메일 검증 로직
        Optional<User> findUserOpt = userRepository.findUserByEmail(email);
        if (findUserOpt.isEmpty()) {
            log.debug("find-password 과정, 유저 존재 X");
            throw new UserNotExistException(message);
        }

        String uuid = UUID.randomUUID().toString();
        redisService.save(uuid, email, 10, TimeUnit.MINUTES);
        emailService.sendPasswordFindEmail(email, uuid);
        log.info("Password change request email sent, UserEmail={}", email);
    }

    // auth token 기반 으로 검증
    public void changePassword(String authToken, String password) {
        String userEmail = redisService.find(authToken);
        if (userEmail == null) {
            throw new IllegalArgumentException("인증 토큰이 만료되었거나 유효하지 않습니다.");
        }
        String encodedPwd = passwordEncoder.encode(password);
        User findUserByEmail = userRepository.findUserByEmail(userEmail).orElseThrow(() -> new UserNotExistException(message));
        findUserByEmail.setPwd(encodedPwd);
        log.info("User Password Changed, UserEmail={}", userEmail);
    }
}
