package net.univwork.api.api_v1.security;

import lombok.RequiredArgsConstructor;
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

        User user = userRepository.findUserByEmail(username).orElseThrow(() -> new BadCredentialsException(badCredentialsMessage));

        if (passwordEncoder.matches(pwd, user.getPwd())) {
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
