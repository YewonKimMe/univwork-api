package net.univwork.api.api_v1.security.customfilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.univwork.api.api_v1.security.SecurityConstants;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Jwt token 검증 필터, 인증 로직 전에 수행
 * */
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // jwt 요청 헤더에서 획득
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        if (jwt != null) { // jwt != null 인 경우, 즉 로그인 한 사용자
            try {
                // 기존 비밀키 생성
                SecretKey existingKey = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                        .verifyWith(existingKey) // 기존 비밀키
                        .build()
                        .parseSignedClaims(jwt)// Front 에서 전송한 jwt, 기존 비밀키와 일치 여부 확인, 일치하지 않으면 exception 발생
                        .getPayload();
                // username, authorities 획득
                String username = String.valueOf(claims.get("id"));
                String authorities = (String) claims.get("authorities");

                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)); // generator 에서 (,) 로 구별하여 토큰 생성, 여기서는 콤마로 구별된 문자열을 다시 List authorities 로 변경
                SecurityContextHolder.getContext().setAuthentication(auth); // 인증 절차 완료, contextholder 에 저장
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received.");
            }

        }
        // jwt == null 인 경우, 비로그인 상황 -> 그냥 다음 필터 실행.
        filterChain.doFilter(request, response);
    }

    // JwtTokenValidatorFilter 는 토큰을 검증하는 필터로 로그인 상황에서는 토큰이 없으므로 실행되지 말아야 함.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return pathMatcher.match("/api/v1/login/**", path); // 로그인 과정에서 실행 X
    }
}
