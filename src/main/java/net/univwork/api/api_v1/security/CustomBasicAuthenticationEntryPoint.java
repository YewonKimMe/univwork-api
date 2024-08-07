package net.univwork.api.api_v1.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.response.ErrorResultAndMessage;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.tool.IpTool;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

        String requestIp = IpTool.getIpAddr(request);
        // Basic Authentication Token 에서 Username 획득
        String username = "Anonymous";
        String base64Credentials = null;
        String decodedCredentials = null;

        String authorizationHeader = request.getHeader("Authorization");
        if (null != authorizationHeader) {
            base64Credentials = authorizationHeader.substring("Basic ".length());
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            decodedCredentials = new String(decodedBytes, StandardCharsets.UTF_8);
            username = decodedCredentials.substring(0, decodedCredentials.indexOf(":"));
        }


        log.debug("AuthenticationEntryPoint, unauthenticated request detected");
        log.info("[AuthenticationEntryPoint] unauthenticated request detected, Path={}, Username={}, IP-Address={}", request.getServletPath(), username, requestIp);
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
