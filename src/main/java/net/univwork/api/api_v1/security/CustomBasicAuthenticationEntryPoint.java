package net.univwork.api.api_v1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.response.ErrorResultAndMessage;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterPropertiesSet() {
        setRealmName("Basic");
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.debug("AuthenticationEntryPoint, unauthenticated request detected");
        log.info("AuthenticationEntryPoint, unauthenticated request detected, path={}", request.getServletPath());
        Exception e = (Exception) request.getAttribute("exception");
        if (e != null) {
            log.debug("exception: {}", e.getMessage());
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ResultAndMessage messageObj = new ErrorResultAndMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase(), authException.getMessage());
        String resultMessage = objectMapper.writeValueAsString(messageObj);
        response.getWriter().write(resultMessage);
    }
}
