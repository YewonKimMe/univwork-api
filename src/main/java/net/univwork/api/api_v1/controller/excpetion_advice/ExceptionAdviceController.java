package net.univwork.api.api_v1.controller.excpetion_advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.response.ErrorResultAndMessage;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.exception.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdviceController {

    private final MessageSource ms; // message.properties 접근용


    /**
     * API 예외처리 핸들러_IllegalArgumentException
     * @param e IllegalArgumentException
     * @return ErrorResultAndMessage
     * @apiNote 잘못된 인자가 전달된 경우 IllgalArgumentException 발생
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultAndMessage> illegalArgEx(IllegalArgumentException e) {
        log.debug("IllegalExMessage={}", e.getMessage());
        ErrorResultAndMessage errorResultAndMessage = new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResultAndMessage);
    }

    /**
     * API 예외처리 핸들러_NoUserCodeException
     * @param e NoUserCodeException
     * @return ErrorResultAndMessage
     * @apiNote POST, PUT, PATCH, DELETE HTTP 요청 시 USER_COOKIE 가 없을 경우 <br>AOP @Around 에서 발생
     * @see net.univwork.api.api_v1.aop.UserCheckAop AOP 발생 예시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(NoUserCodeException.class)
    public ResponseEntity<ResultAndMessage> noUserCode(NoUserCodeException e) {
        log.debug("NoUserCodeExMessage={}", e.getMessage());

        ErrorResultAndMessage errorResultAndMessage = new ErrorResultAndMessage(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResultAndMessage);
    }

    /**
     * API 예외처리 핸들러_BlockedClientException
     * @param e BlockedClientException
     * @return ErrorResultAndMessage
     * @apiNote POST, PUT, PATCH, DELETE HTTP 요청 시 차단된 uuid 또는 ip 인 경우<br>AOP @Around 에서 발생
     * @see net.univwork.api.api_v1.aop.UserCheckAop AOP 발생 예시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(BlockedClientException.class)
    public ResponseEntity<ResultAndMessage> blockedCatch(BlockedClientException e) {
        log.debug("BlockedClientException={}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.FORBIDDEN.getReasonPhrase(), e.getMessage()));
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
    public ResponseEntity<ResultAndMessage> badCredential(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 PasswordNotMatchException
     * @param e PasswordNotMatchException
     * @return ErrorResultAndMessage
     * @apiNote 비밀번호 불일치
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(PasswordNotMatchException.class)
    public ResponseEntity<ResultAndMessage> passwordNotMatches(PasswordNotMatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 DuplicationException
     * @param e DuplicationException
     * @return ErrorResultAndMessage
     * @apiNote DuplicationException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<ResultAndMessage> duplication(DuplicationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 NoRepeatException
     * @param e NoRepeatException
     * @return ErrorResultAndMessage
     * @apiNote NoRepeatException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(NoRepeatException.class)
    public ResponseEntity<ResultAndMessage> noRepeat(NoRepeatException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 AlreadyReportedException
     * @param e AlreadyReportedException
     * @return ErrorResultAndMessage
     * @apiNote AlreadyReportedException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(AlreadyReportedException.class)
    public ResponseEntity<ResultAndMessage> alreadyReported(AlreadyReportedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 EmailAlreadyExistException
     * @param e EmailAlreadyExistException
     * @return ErrorResultAndMessage
     * @apiNote EmailAlreadyExistException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ResultAndMessage> emailAlreadyExist(EmailAlreadyExistException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 NoAuthenticationException
     * @param e NoAuthenticationException
     * @return ErrorResultAndMessage
     * @apiNote NoAuthenticationException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(NoAuthenticationException.class)
    public ResponseEntity<ResultAndMessage> noAuthentication(NoAuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 UnivEmailNotFountException
     * @param e UnivEmailNotFountException
     * @return ErrorResultAndMessage
     * @apiNote UnivEmailNotFountException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(UnivEmailNotFountException.class)
    public ResponseEntity<ResultAndMessage> univEmailNotFound(UnivEmailNotFountException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 UserAlreadyExistException
     * @param e UserAlreadyExistException
     * @return ErrorResultAndMessage
     * @apiNote UserAlreadyExistException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ResultAndMessage> userAlreadyExist(UserAlreadyExistException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 UserNotExistException
     * @param e UserNotExistException
     * @return ErrorResultAndMessage
     * @apiNote UserNotExistException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(UserNotExistException.class)
    public ResponseEntity<ResultAndMessage> userNotExist(UserNotExistException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 DomainNotMatchException
     * @param e DomainNotMatchException
     * @return ErrorResultAndMessage
     * @apiNote DomainNotMatchException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(DomainNotMatchException.class)
    public ResponseEntity<ResultAndMessage> userNotExist(DomainNotMatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage()));
    }

    /**
     * API 예외처리 NoticeNotFoundException
     * @param e NoticeNotFoundException
     * @return ErrorResultAndMessage
     * @apiNote NoticeNotFoundException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(NoticeNotFoundException.class)
    public ResponseEntity<ResultAndMessage> noticeNotFound(NoticeNotFoundException e) {
        ErrorResultAndMessage errorResultAndMessage = new ErrorResultAndMessage(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResultAndMessage);
    }

    /**
     * API 예외처리 ExcelFileAdditionException
     * @param e ExcelFileAdditionException
     * @return ErrorResultAndMessage
     * @apiNote ExcelFileAdditionException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(ExcelFileAdditionException.class)
    public ResponseEntity<ResultAndMessage> excelFileAddedEx(ExcelFileAdditionException e) {
        ErrorResultAndMessage errorResultAndMessage = new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResultAndMessage);
    }

    /**
     * API 예외처리 ExcelFileAdditionException
     * @param e ExcelFileAdditionException
     * @return ErrorResultAndMessage
     * @apiNote ExcelFileAdditionException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(NoCookieValueException.class)
    public ResponseEntity<ResultAndMessage> noCookieValue(NoCookieValueException e) {
        ErrorResultAndMessage errorResultAndMessage = new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResultAndMessage);
    }

    /**
     * API 예외처리 핸들러_범용_RuntimeException
     * @param e RuntimeException
     * @return ErrorResultAndMessage
     * @apiNote RuntimeException 발생 시
     * @see ErrorResultAndMessage
     * */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResultAndMessage> runtimeException(RuntimeException e) {
        log.debug("RuntimeException={}", e.getMessage());
        ErrorResultAndMessage errorResponse = new ErrorResultAndMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "서버에 오류가 발생했습니다."
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultAndMessage> internalServerError(Exception e) {
        log.debug("Exception={}", e.getMessage());
        ErrorResultAndMessage errorResponse = new ErrorResultAndMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "서버에 오류가 발생했습니다."
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}
