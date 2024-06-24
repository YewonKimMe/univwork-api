package net.univwork.api.api_v1.aop;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.BlockedIp;
import net.univwork.api.api_v1.domain.entity.BlockedUser;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.exception.BlockedClientException;
import net.univwork.api.api_v1.exception.NoRepeatException;
import net.univwork.api.api_v1.exception.NoUserCodeException;
import net.univwork.api.api_v1.service.BlockedService;
import net.univwork.api.api_v1.tool.CookieUtils;
import net.univwork.api.api_v1.tool.IpTool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class UserCheckAop {

    private final BlockedService blockedService;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    /**
     * 모든 요청에 대해 적용
     * */
    @Pointcut("execution(* net.univwork.api.api_v1.controller..*(..))")
    public void userCookieCheckAndCert(){}

    /**
     * POST, DELETE, PUT, PATCH 요청에만 적용
     * */
    @Pointcut("(@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.PatchMapping)) && execution(* net.univwork.api.api_v1.controller..*(..))")
    public void userAuthCheck() {}

    /**
     * <p>userCheck: 유저, IP 검증 AOP 메소드</p>
     * @apiNote POST, PUT, PATCH, DELETE HTTP 메소드 요청에서 유저 쿠키가 존재하는지 확인,<br>USER_COOKIE가 존재하지 않을 경우 NoUserCodeException 발생,<br>쿠키 검증 및 IP 확인<br>
     * @since 1.0.0
     * */
    @Around("userAuthCheck()")
    public Object userCheck(ProceedingJoinPoint joinPoint) throws Throwable {

        // USER_COOKIE 가 존재하지 않을 경우, 예외 발생
//        if (!CookieUtils.checkCookie(request, CookieName.USER_COOKIE)) {
//            throw new NoUserCodeException("user uuid does not exist in the request.");
//        }

        // 사전 차단된 유저 검증 로직, 쿠키로 확인
//        if (CookieUtils.checkCookie(request, CookieName.BLOCKED_FIRST_CHECK_COOKIE)) {
//            log.debug("blocked first detected={}", new Timestamp(System.currentTimeMillis()));
//            throw new BlockedClientException("already blocked user");
//        }

        // 반복하여 등록했을 경우
        if (CookieUtils.checkCookie(request, CookieName.REPEAT_REQUEST)) {
            throw new NoRepeatException("짧은 시간 내에 반복하여 등록하실 수 없습니다. 잠시 후 다시 시도해 주세요.");
        }


        // 유저 검증 로직
        String userNameUuidCookie = CookieUtils.getUserCookie(request, CookieName.USER_COOKIE);
        BlockedUser blockedUser = blockedService.findBlockedUser(userNameUuidCookie);
        if (blockedUser != null && blockedUser.getBlockedUser().equals(userNameUuidCookie)) {
            //setBlockCookie(response); // 사전 차단용 쿠키를 세팅
            throw new BlockedClientException("blocked user uuid");
        }

        // IP 주소 검증 로직
        String userIpAddr = IpTool.getIpAddr(request);
        BlockedIp blockedIp = blockedService.findBlockedIp(userIpAddr);
        if (blockedIp != null && blockedIp.getBlockedIp().equals(userIpAddr)) {
            //setBlockCookie(response); // 사전 차단용 쿠키를 세팅
            throw new BlockedClientException("blocked user ip");
        }

        // 반복 등록 방지용 쿠키 생성
        //setRepeatCookie(response);
        return joinPoint.proceed();
    }

    private void setBlockCookie(HttpServletResponse response) {
        // 사전 차단용 쿠키를 생성
        Cookie cookie = new Cookie(CookieName.BLOCKED_FIRST_CHECK_COOKIE.getCookieName(), UUID.randomUUID().toString());
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(30)); // 쿠키 유효기간은 30일
        cookie.setPath("/"); // /api/v1 경로 이하에 모두 적용
        response.addCookie(cookie);
    }

    private void setRepeatCookie(HttpServletResponse response) {
        // 반복 등록 방지용 쿠키 생성
        Cookie cookie = new Cookie(CookieName.REPEAT_REQUEST.getCookieName(), UUID.randomUUID().toString().replaceAll("-", "."));
        cookie.setMaxAge((int) TimeUnit.SECONDS.toSeconds(20));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
