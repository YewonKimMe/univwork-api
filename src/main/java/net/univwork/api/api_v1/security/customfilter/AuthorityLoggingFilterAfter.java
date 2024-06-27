package net.univwork.api.api_v1.security.customfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.sql.Timestamp;

@Slf4j
public class AuthorityLoggingFilterAfter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("[유저명: {}, 접속 시간: {}, 권한: {}]", authentication.getName(), new Timestamp(System.currentTimeMillis()), authentication.getAuthorities());
        }
        filterChain.doFilter(request, response);
    }

    // 최초 로그인 요청이 아닌 경우에 실행되지 않음
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        log.debug("pathMatcher?={}", !pathMatcher.match("/api/v1/login/**", path));

        return !pathMatcher.match("/api/v1/login/**", path);
    }
}
