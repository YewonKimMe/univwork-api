package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.SignUpEmailDto;
import net.univwork.api.api_v1.domain.dto.SignUpFormDto;
import net.univwork.api.api_v1.domain.dto.SignUpIdDto;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.enums.Role;
import net.univwork.api.api_v1.enums.UnivEmailDomain;
import net.univwork.api.api_v1.exception.EmailAlreadyExistException;
import net.univwork.api.api_v1.exception.UserAlreadyExistException;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class SingUpService {

    private final EmailService emailService;

    private final JpaUserRepository userRepository;

    private final RedisService redisService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(SignUpFormDto signUpFormDto) {

        String hashedPw = passwordEncoder.encode(signUpFormDto.getPassword());
        // 유저 엔티티 생성
        User user = User.builder()
                .userId(signUpFormDto.getId())
                .email(signUpFormDto.getEmail())
                .pwd(hashedPw).role(Role.PREFIX.getRole() + Role.USER.getRole())
                .createDate(new Timestamp(System.currentTimeMillis()))
                .verification(false)
                .build();

        // 유저 등록
        userRepository.save(user);

        log.info("[User create - id: {}, date: {}]", signUpFormDto.getId(), new Timestamp(System.currentTimeMillis()));

        // uuid 생성 후 redis 에 저장
        String authToken = UUID.randomUUID().toString();
        redisService.save(authToken, signUpFormDto.getEmail(), 48, TimeUnit.HOURS);
        // 이메일 발송
        emailService.sendVerifyEmail(signUpFormDto.getEmail(), authToken);
    }

    public String checkValidUnivEmailAddr(SignUpEmailDto emailDto) {

        String email = emailDto.getEmail();

        log.debug("email={}", email);

        // 이메일 도메인 획득
        String domain = email.split("@")[1];
        log.debug("domain={}", domain);
        // 학교 메일 유효 여부 확인, 학교명 반환
        return UnivEmailDomain.checkDomainFromString(domain);
    }

    public void checkDuplicateEmail(SignUpEmailDto emailDto) {

        String email = emailDto.getEmail();

        log.debug("email={}", email);

        // 유저 저장소 에서 이메일 중복 체크
        Optional<User> findUser = userRepository.findUserByEmail(email);
        if (findUser.isPresent()) {
            throw new EmailAlreadyExistException("이미 사용중인 이메일 입니다.");
        }
    }

    public void checkDuplicatedId(SignUpIdDto idDto) {

        String id = idDto.getId();

        log.debug("id={}", id);

        // 유저 저장소 에서 아이디 중복 체크
        Optional<User> findUser = userRepository.findUserByUserId(id);
        if (findUser.isPresent()) {
            throw new UserAlreadyExistException("이미 사용중인 아이디 입니다.");
        }
    }

}
