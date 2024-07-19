package net.univwork.api.api_v1.controller.excpetion_advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.response.ErrorResultAndMessage;
import net.univwork.api.api_v1.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdviceController {

    private final MessageSource ms; // message.properties 접근용

    /**
     * API 예외 처리 핸들러_MethodArgumentTypeMismatchException
     * @param e MethodArgumentTypeMismatchException
     * @return ErrorResultAndMessage
     * @see ErrorResultAndMessage
     * @apiNote HTTP 요청 시 Method Parameter 에 잘못된 타입의 인자를 전달한 경우 MethodargumentTypeMismatchException 발생
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResultAndMessage methodArgTypeMis(MethodArgumentTypeMismatchException e) {
        log.debug("[exceptionHandler] ex", e);
        // messageSource 에서 message 획득, 틀린 파라미터 인덱스와 파라미터 타입
        String message = ms.getMessage("exception.MethodArgumentTypeMismatchExceptionMessage", new Object[]{e.getParameter().getParameterIndex(), e.getParameter().getParameterType()}, Locale.KOREA);

        log.debug("message={}", message);
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
    }

    /**
     * API 예외처리 핸들러_IllegalArgumentException
     * @param e IllegalArgumentException
     * @return ErrorResultAndMessage
     * @apiNote 잘못된 인자가 전달된 경우 IllgalArgumentException 발생
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResultAndMessage illegalArgEx(IllegalArgumentException e) {
        log.debug("IllegalExMessage={}", e.getMessage());

        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 핸들러_NoUserCodeException
     * @param e NoUserCodeException
     * @return ErrorResultAndMessage
     * @apiNote POST, PUT, PATCH, DELETE HTTP 요청 시 USER_COOKIE 가 없을 경우 <br>AOP @Around 에서 발생
     * @see net.univwork.api.api_v1.aop.UserCheckAop AOP 발생 예시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoUserCodeException.class)
    public ErrorResultAndMessage noUserCode(NoUserCodeException e) {
        log.debug("NoUserCodeExMessage={}", e.getMessage());

        return new ErrorResultAndMessage(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 핸들러_BlockedClientException
     * @param e BlockedClientException
     * @return ErrorResultAndMessage
     * @apiNote POST, PUT, PATCH, DELETE HTTP 요청 시 차단된 uuid 또는 ip 인 경우<br>AOP @Around 에서 발생
     * @see net.univwork.api.api_v1.aop.UserCheckAop AOP 발생 예시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(BlockedClientException.class)
    public ErrorResultAndMessage blockedCatch(BlockedClientException e) {
        log.debug("BlockedClientException={}", e.getMessage());
        return new ErrorResultAndMessage(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 핸들러_BadCredentialsException
     * @param e BadCredentialsException
     * @return ErrorResultAndMessage
     * @apiNote 인증 실패 시 발생
     * @see net.univwork.api.api_v1.security.CustomAuthenticationProvider#authenticate(Authentication) 
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadCredentialsException.class)
    public ErrorResultAndMessage badCredential(BadCredentialsException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 PasswordNotMatchException
     * @param e PasswordNotMatchException
     * @return ErrorResultAndMessage
     * @apiNote 비밀번호 불일치
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordNotMatchException.class)
    public ErrorResultAndMessage passwordNotMatches(PasswordNotMatchException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 DuplicationException
     * @param e DuplicationException
     * @return ErrorResultAndMessage
     * @apiNote DuplicationException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DuplicationException.class)
    public ErrorResultAndMessage duplication(DuplicationException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 NoRepeatException
     * @param e NoRepeatException
     * @return ErrorResultAndMessage
     * @apiNote NoRepeatException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoRepeatException.class)
    public ErrorResultAndMessage noRepeat(NoRepeatException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 AlreadyReportedException
     * @param e AlreadyReportedException
     * @return ErrorResultAndMessage
     * @apiNote AlreadyReportedException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyReportedException.class)
    public ErrorResultAndMessage alreadyReported(AlreadyReportedException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 EmailAlreadyExistException
     * @param e EmailAlreadyExistException
     * @return ErrorResultAndMessage
     * @apiNote EmailAlreadyExistException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ErrorResultAndMessage emailAlreadyExist(EmailAlreadyExistException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 NoAuthenticationException
     * @param e NoAuthenticationException
     * @return ErrorResultAndMessage
     * @apiNote NoAuthenticationException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NoAuthenticationException.class)
    public ErrorResultAndMessage noAuthentication(NoAuthenticationException e) {
        return new ErrorResultAndMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 UnivEmailNotFountException
     * @param e UnivEmailNotFountException
     * @return ErrorResultAndMessage
     * @apiNote UnivEmailNotFountException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnivEmailNotFountException.class)
    public ErrorResultAndMessage univEmailNotFound(UnivEmailNotFountException e) {
        return new ErrorResultAndMessage(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 UserAlreadyExistException
     * @param e UserAlreadyExistException
     * @return ErrorResultAndMessage
     * @apiNote UserAlreadyExistException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistException.class)
    public ErrorResultAndMessage userAlreadyExist(UserAlreadyExistException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 UserNotExistException
     * @param e UserNotExistException
     * @return ErrorResultAndMessage
     * @apiNote UserNotExistException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotExistException.class)
    public ErrorResultAndMessage userNotExist(UserNotExistException e) {
        return new ErrorResultAndMessage(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 DomainNotMatchException
     * @param e DomainNotMatchException
     * @return ErrorResultAndMessage
     * @apiNote DomainNotMatchException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DomainNotMatchException.class)
    public ErrorResultAndMessage userNotExist(DomainNotMatchException e) {
        return new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 NoticeNotFoundException
     * @param e NoticeNotFoundException
     * @return ErrorResultAndMessage
     * @apiNote NoticeNotFoundException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoticeNotFoundException.class)
    public ErrorResultAndMessage noticeNotFound(NoticeNotFoundException e) {
        return new ErrorResultAndMessage(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
    }

    /**
     * API 예외처리 핸들러_범용_RuntimeException
     * @param e RuntimeException
     * @return ErrorResultAndMessage
     * @apiNote RuntimeException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResultAndMessage runtimeException(RuntimeException e) {
        log.debug("RuntimeException={}", e.getMessage());
        return new ErrorResultAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "서버에 오류가 발생했습니다.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResultAndMessage internalServerError(Exception e) {
        log.debug("Exception={}", e.getMessage());
        return new ErrorResultAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "서버에 오류가 발생했습니다.");
    }
}
