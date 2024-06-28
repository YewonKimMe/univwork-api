package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.*;
import net.univwork.api.api_v1.domain.entity.Authority;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.enums.Role;
import net.univwork.api.api_v1.enums.UnivEmailDomain;
import net.univwork.api.api_v1.exception.EmailAlreadyExistException;
import net.univwork.api.api_v1.exception.UserAlreadyExistException;
import net.univwork.api.api_v1.exception.UserNotExistException;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
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

        String hashedPw = passwordEncoder.encode(signUpFormDto.getPassword()); // 비밀번호 해싱
        String userUnivDomain = signUpFormDto.getEmail().split("@")[1]; // 이메일 도메인 획득

        // 유저 엔티티 생성
        User user = User.builder()
                .userId(signUpFormDto.getId())
                .email(signUpFormDto.getEmail())
                .pwd(hashedPw)
                .role(Role.PREFIX.getRole() + Role.USER.getRole())
                .createDate(new Timestamp(System.currentTimeMillis()))
                .verification(false)
                .univDomain(userUnivDomain)
                .authorities(new HashSet<>())
                .build();

        // Authority 생성
        Authority authority = Authority.builder()
                .name("ROLE_USER")
                .build();

        // 양방향 관계 설정
        authority.setUser(user); // user와 authority 연결
        user.getAuthorities().add(authority); // user에 authority 추가

        // 유저 등록
        userRepository.save(user); // save 후에 user 객체에 자동으로 생성된 no 값이 채워짐

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

    @Transactional
    public EmailVerificationDto verify(final String authToken) {

        log.debug("authToken={}", authToken);

        // redisService 에서 authToken 으로 이메일을 찾아오고
        String email = redisService.find(authToken);

        if (email == null) {
            String errorMessage = "인증키가 유효하지 않거나 인증키가 만료 되었습니다.";

            throw new IllegalArgumentException(errorMessage);
        }

        // 찾은 이메일로 User 의 verify = 1 설정 후, User 획득
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UserNotExistException("이메일로 유저를 찾지 못했습니다."));
        user.setVerification(true);

        // 획득한 User 의 로그인 아이디와 이메일을 dto에 매핑
        EmailVerificationDto emailVerificationDto = EmailVerificationDto.builder()
                .loginId(user.getUserId())
                .email(user.getEmail())
                .build();

        // redis 에서 authToken 삭제
        redisService.delete(authToken);

        return emailVerificationDto;
    }

}
