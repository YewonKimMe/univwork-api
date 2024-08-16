package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.tool.CookieUtils;
import net.univwork.api.api_v1.tool.IpTool;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Connection Check", description = "유저 접속 확인")
@RestController
@RequestMapping(value = "/api/v1/connection", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConnectionCheckController {

    @GetMapping
    public ResponseEntity<ResultAndMessage> checkInitialConnect(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if (CookieUtils.checkCookie(request, CookieName.INITIAL_CONNECTION_COOKIE)) {
            return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "already connect"));
        }
        Cookie initialConnetCheckCookie = new Cookie(CookieName.INITIAL_CONNECTION_COOKIE.getCookieName(), "1");
        initialConnetCheckCookie.setPath("/");
        initialConnetCheckCookie.setMaxAge(-1); // 브라우저 종료 시 삭제

        response.addCookie(initialConnetCheckCookie);
        String ipAddr = IpTool.getIpAddr(request);

        log.info("[유저 최초 접속] IP-Address={}, Time={}", ipAddr, new Timestamp(System.currentTimeMillis()));
        return ResponseEntity
                .ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "initial connect"));
    }
}
