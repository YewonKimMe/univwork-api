package net.univwork.api.api_v1.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.Authority;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final JpaUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final String badCredentialsMessage = "아이디 또는 비밀번호를 확인해 주세요.";

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();

        String pwd = authentication.getCredentials().toString();

        User user = userRepository.findUserByUserId(username).orElseThrow(() -> new BadCredentialsException(badCredentialsMessage));
        log.debug("findUser={}", user.toString());
        if (!user.isVerification()) {
            log.debug("이메일 인증이 필요합니다. 아이디={}", username);
            throw new BadCredentialsException("최초 이메일 인증 이후에 로그인이 가능합니다.");
        }

        if (passwordEncoder.matches(pwd, user.getPwd())) {
            log.debug("로그인 성공, ID={}", username);
            return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(user.getAuthorities()));
        }

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
