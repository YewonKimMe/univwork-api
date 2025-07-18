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
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Connection Check", description = "유저 접속 확인")
@RestController
@RequestMapping(value = "/api/v1/connection", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConnectionCheckController {

    @GetMapping
    public ResponseEntity<ResultAndMessage> checkInitialConnect(HttpServletRequest request, HttpServletResponse response) {

        if (CookieUtils.checkCookie(request, CookieName.INITIAL_CONNECTION_COOKIE)) {
            return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "already connect"));
        }

        checkAndSetUserCookie(request, response);

        setInitialConnectCookie(response);

        setCommentCountingCookie(response);

        String ipAddr = IpTool.getIpAddr(request);
        if (ipAddr.contains(",")) {
            ipAddr = ipAddr.split(",")[0];
        }

        log.info("[유저 최초 접속] IP-Address={}, Time={}", ipAddr, new Timestamp(System.currentTimeMillis()));
        return ResponseEntity
                .ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "initial connect"));
    }

    private void checkAndSetUserCookie(HttpServletRequest request, HttpServletResponse response) {
        if (!CookieUtils.checkCookie(request, CookieName.USER_COOKIE)) {
            Cookie cookie = new Cookie(CookieName.USER_COOKIE.getCookieName(), UUID.randomUUID().toString());
            cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(30)); // 쿠키 유효기간은 30일
            cookie.setPath("/"); // / 경로 이하에 모두 적용
            response.addCookie(cookie);
            log.info("[Connection Controller - 신규 익명 유저: 유저명={}]", cookie.getValue());
        }

    }

    private void setInitialConnectCookie(HttpServletResponse response) {
        Cookie initialConnetCheckCookie = new Cookie(CookieName.INITIAL_CONNECTION_COOKIE.getCookieName(), "1");
        initialConnetCheckCookie.setPath("/");
        initialConnetCheckCookie.setMaxAge(-1); // 브라우저 종료 시 삭제
        response.addCookie(initialConnetCheckCookie);
    }

    private void setCommentCountingCookie(HttpServletResponse response) {
        Cookie commentCookie = new Cookie(CookieName.WORKPLACE_COMMENT_COOKIE.getCookieName(), "s");
        commentCookie.setPath("/");
        commentCookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(14)); // 댓글 쿠키, 7일짜리

        response.addCookie(commentCookie);
    }
}
