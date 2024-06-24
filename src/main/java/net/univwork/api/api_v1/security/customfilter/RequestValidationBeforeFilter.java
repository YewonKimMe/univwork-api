package net.univwork.api.api_v1.security.customfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RequestValidationBeforeFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION_SCHEME_BASIC = "Basic"; // Front 에서 보내는 헤더의 시작 부분

    private final Charset charset = StandardCharsets.UTF_8;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 예제, 그냥 예시임 나중에 지우기
        String header = request.getHeader(HttpHeaders.AUTHORIZATION); // 헤더 획득

        // Front 단에서 "Basic " + "email(base64인코딩)" + ":" + "password(base64인코딩)" 으로 Authentication 요청 헤더를 보냄

        if (StringUtils.hasText(header)) {
            header = header.trim();
            if (StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
                byte[] byte64Token = header.substring(6).getBytes(charset);
            }
        }
    }
}
