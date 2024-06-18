package net.univwork.api.api_v1.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.tool.ConstString;
import net.univwork.api.api_v1.tool.CookieUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserCheckInterceptor implements HandlerInterceptor {


    /**
     * 유저 확인 및 검증 인터셉터
     * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!CookieUtils.checkCookie(request, CookieName.USER_COOKIE)) {
            Cookie cookie = new Cookie(ConstString.USER_COOKIE, UUID.randomUUID().toString());
            cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(30)); // 쿠키 유효기간은 30일
            cookie.setPath("/"); // / 경로 이하에 모두 적용
            response.addCookie(cookie);
            log.info("[신규 유저 확인: 유저명={}, 시간={}]", cookie.getValue(), new Timestamp(System.currentTimeMillis()));
        }

        return true;
    }
}
