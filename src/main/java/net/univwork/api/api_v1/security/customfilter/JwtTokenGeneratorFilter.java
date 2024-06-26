package net.univwork.api.api_v1.security.customfilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.univwork.api.api_v1.security.SecurityConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * JWT Token 생성 필터, BasicAuthenticationFilter 의 인증 과정이 끝난 이후 Jwt token 을 생성하고 응답에 보내기 위한 필터
 * */
public class JwtTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {

            SecretKey secretKey = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
            Instant expirationTime = Instant.now().plus(Duration.ofHours(3)); // 3시간뒤 만료
            String jwt = Jwts.builder()
                    .issuer("univwork.net")
                    .subject("Login Jwt Token")
                    // 이메일 등은 나중에 바꾸기. user 객체 자체를 바꿔야 할 수도
                    .claim("id", authentication.getName())
                    .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                    .issuedAt(new Date())
                    .expiration(Date.from(expirationTime))
                    .signWith(secretKey)
                    .compact(); // jwt 생성

            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }
        filterChain.doFilter(request, response);
    }

    // 로그인 과정에서만 JWT 토큰이 생성되도록 필터 실행 여부를 결정
    // 이부분은 공부가 더 필요할듯

    // JwtTokenGeneratorFilter 는 로그인 상황에서만 실행되어야 함 -> login 이 아닌 경우에는 true, 실행되지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return !pathMatcher.match("/api/v1/login/**", path);
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collections) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collections) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }
}
