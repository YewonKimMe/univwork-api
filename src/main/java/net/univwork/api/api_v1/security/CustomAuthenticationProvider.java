package net.univwork.api.api_v1.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.Authority;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import net.univwork.api.api_v1.service.EmailService;
import net.univwork.api.api_v1.service.RedisService;
import net.univwork.api.api_v1.tool.TimestampTool;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final JpaUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RedisService redisService;

    private final EmailService emailService;

    private final String badCredentialsMessage = "아이디 또는 비밀번호를 확인해 주세요.";

    private final String validateValue = "NEED_VERIFY";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();

        String pwd = authentication.getCredentials().toString();

        log.debug("username={}, pwd={}", username, pwd);
        log.debug("login 검증 시작");
        Optional<User> userOpt = userRepository.findUserByUserId(username);
        if (userOpt.isEmpty()) {
            log.debug("아이디가 잘못 입력됨");
            throw new BadCredentialsException(badCredentialsMessage);
        }
        User user = userOpt.get();
        log.debug("findUser={}", user.toString());
        if (!user.isVerification()) {
            log.debug("이메일 인증이 필요합니다. 아이디={}", username);
            log.info("[이메일 인증 필요] 이메일 인증이 필요한 계정, 아이디={}", username);
            String findVerifyValue = redisService.find(user.getEmail());
            log.debug("findVerifyValue={}", findVerifyValue);
            if (findVerifyValue == null) {
                Timestamp createDate = user.getCreateDate();
                Timestamp now = Timestamp.from(Instant.now());
                if (TimestampTool.calculateHowMillisecondsDiffEndToStart(createDate, now, TimeUnit.HOURS.toMillis(24))) {
                    log.info("[가입 후 24시간 경과한 유저] 인증메일 재발송, Username={}", username);
                    int time = 3;
                    String authToken = UUID.randomUUID().toString();
                    redisService.saveIfAbsent(authToken, user.getEmail(), time, TimeUnit.HOURS);
                    emailService.sendVerifyEmail(user.getEmail(), authToken, time);

                    // 중복 발송 방지를 위해서 email 을 redis 에 key 로 저장
                    redisService.saveIfAbsent(user.getEmail(), validateValue, time, TimeUnit.HOURS);
                    throw new BadCredentialsException("등록 이후 24시간 경과 후에도 이메일 계정이 인증되지 않았습니다.\n가입 시 등록한 학교 이메일 주소로 인증 메일이 재발송 되었습니다.");
                }
            }else if (findVerifyValue.equals(validateValue)) {
                throw new BadCredentialsException("최초 이메일 인증 이후에 로그인이 가능합니다.\n추가 인증 메일이 이미 발송되었습니다.\n이메일 발송 까지 최대 5분이 소요될 수 있습니다.\n이메일이 도착하지 않았다면 관리자에게 문의해 주세요.");
            }
            throw new BadCredentialsException("최초 이메일 인증 이후에 로그인이 가능합니다.");
        }
        if (user.isBlockedFlag()) {
            throw new BadCredentialsException("차단된 계정입니다. 운영자에게 문의 하세요.");
        }
        if (passwordEncoder.matches(pwd, user.getPwd())) {
            log.debug("로그인 성공, ID={}", username);
            return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(user.getAuthorities()));
        }
        log.debug("로그인 실패");
        throw new BadCredentialsException(badCredentialsMessage);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }
}
