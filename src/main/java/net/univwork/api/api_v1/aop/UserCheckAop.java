package net.univwork.api.api_v1.aop;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.Authority;
import net.univwork.api.api_v1.domain.entity.BlockedIp;
import net.univwork.api.api_v1.domain.entity.BlockedUser;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.enums.Role;
import net.univwork.api.api_v1.exception.BlockedClientException;
import net.univwork.api.api_v1.exception.NoAuthenticationException;
import net.univwork.api.api_v1.exception.NoCookieValueException;
import net.univwork.api.api_v1.exception.NoRepeatException;
import net.univwork.api.api_v1.service.BlockedService;
import net.univwork.api.api_v1.service.UserService;
import net.univwork.api.api_v1.tool.CookieUtils;
import net.univwork.api.api_v1.tool.IpTool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class UserCheckAop {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final UserService userService;

    private final BlockedService blockedService;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            username = authentication.getName();
            log.debug("username={}", username);
        } else {
            log.debug("AOP 인증 정보 미존재");
            throw new NoAuthenticationException("인증 정보가 없습니다.");
        }
        log.debug("userAuthCheck aspect");
        // 반복하여 등록했을 경우
        if (CookieUtils.checkCookie(request, CookieName.REPEAT_REQUEST)) {
            throw new NoRepeatException("짧은 시간 내에 반복하여 요청 할 수 없습니다.\n잠시 후 다시 시도해 주세요.");
        }
        // 유저 검증 로직
        String userNameUuidCookie = CookieUtils.getUserCookie(request, CookieName.USER_COOKIE);
        if (userNameUuidCookie == null) {
            throw new NoCookieValueException("인증 정보가 없습니다.");
        }
        log.debug("isAnonymousUser?={}", authentication instanceof AnonymousAuthenticationToken);
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User findUser = userService.findUserByUserId(username);
            if (findUser != null && findUser.isBlockedFlag()) {
                Set<Authority> authorities = findUser.getAuthorities();
                boolean isNotAdmin = true;
                for (Authority authority : authorities) {
                    if (authority.getName().equals(Role.PREFIX.getRole() + Role.ADMIN.getRole())) {
                        log.debug("Role.PREFIX.getRole() + Role.ADMIN.getRole() = {}", Role.PREFIX.getRole() + Role.ADMIN.getRole());

                        log.info("[관리자 요청] 차단 적용 무시, username={}, role={}", authority.getUser().getUserId(), authority.getName());
                        isNotAdmin = false;
                        break;
                    }
                }
                log.error("차단된 유저 감지 = {}", findUser.getUserId());
                //setBlockCookie(response); // 사전 차단용 쿠키를 세팅
                if (isNotAdmin) {
                    throw new BlockedClientException("차단된 계정입니다.");
                }
            }
        } else {
            BlockedUser blockedUser = blockedService.findBlockedUser(userNameUuidCookie);
            log.debug("blockedUser={}", blockedUser.getBlockedUser());
            if (blockedUser != null && blockedUser.getBlockedUser().equals(userNameUuidCookie)) {
                log.error("차단된 익명 유저 감지 = {}", userNameUuidCookie);
                throw new BlockedClientException("차단된 유저 입니다.");
            }
        }

        // IP 주소 검증 로직
        String userIpAddr = IpTool.getIpAddr(request);
        if (userIpAddr.contains(",")) {
            userIpAddr = userIpAddr.split(",")[0];
        }
        BlockedIp blockedIp = blockedService.findBlockedIp(userIpAddr);
        if (blockedIp != null && blockedIp.getBlockedIp().equals(userIpAddr)) {
            setBlockCookie(response); // 사전 차단용 쿠키를 세팅
            throw new BlockedClientException("접근 금지된 IP 입니다.");
        }

        // 반복 등록 방지용 쿠키 생성
        setRepeatCookie(response);
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
        log.debug("response is Committed? = {}", response.isCommitted());
        // 반복 요청 방지용 쿠키 생성
        Cookie cookie = new Cookie(CookieName.REPEAT_REQUEST.getCookieName(), UUID.randomUUID().toString().replaceAll("-", "."));
        cookie.setMaxAge((int) TimeUnit.SECONDS.toSeconds(5));
        cookie.setPath("/");
        response.addCookie(cookie);
        log.debug("repeat cookie added");
    }

}
