package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.EmailVerificationDto;
import net.univwork.api.api_v1.domain.dto.SignUpEmailDto;
import net.univwork.api.api_v1.domain.dto.SignUpFormDto;
import net.univwork.api.api_v1.domain.dto.SignUpIdDto;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.service.RedisService;
import net.univwork.api.api_v1.service.SingUpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Sign Up", description = "회원가입 관련 기능 엔드포인트")
@Controller
@RequestMapping("/api/v1/sign-up")
public class SignUpController {

    private final SingUpService singUpService;

    private final RedisService redisService;

    @Operation(summary = "회원 등록", description = "회원 등록, 아이디 및 이메일 중복 확인 서브로직 포함")
    @PostMapping("/register")
    public ResponseEntity<SuccessResultAndMessage> signUp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 등록 폼") @Validated @RequestBody SignUpFormDto signUpFormDto,
            @Parameter(hidden = true) BindingResult bindingResult) {

        log.debug("SignUpFormDto={}", signUpFormDto.toString());

        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                sb.append(";");
            }
            throw new IllegalArgumentException(sb.toString());
        }
        String findUserId = redisService.find(signUpFormDto.getId());
        String findUserEmail = redisService.find(signUpFormDto.getEmail());

        log.debug("Redis Checked FindUserId={}", findUserId);
        log.debug("Redis Checked FindUserEmail={}", findUserEmail);

        if (null == findUserId) {
            throw new IllegalArgumentException("아이디(ID) 중복 확인이 필요합니다.");
        }
        if (null == findUserEmail) {
            throw new IllegalArgumentException("이메일(Email) 중복 확인이 필요합니다.");
        }

        singUpService.createUser(signUpFormDto);

        SuccessResultAndMessage result = new SuccessResultAndMessage(HttpStatus.CREATED.getReasonPhrase(), "계정이 생성되었습니다.");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "ID Check", description = "아이디 중복 체크, 회원 등록 전 반드시 수행")
    @PostMapping("/id-check")
    public ResponseEntity<SuccessResultAndMessage> idDuplicateCheck(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ID 객체") @Validated @RequestBody SignUpIdDto idDto,
            @Parameter(hidden = true) BindingResult bindingResult
    ) {

        log.debug("SingUpIdDto={}", idDto.toString());

        if (bindingResult.hasErrors()) {
            String defaultMessage = bindingResult.getFieldError().getDefaultMessage();
            throw new IllegalArgumentException(defaultMessage);
        }
        // 로그인 id 중복 검사
        singUpService.checkDuplicatedId(idDto);

        String id = idDto.getId();

        // redis 에 데이터 저장
        redisService.saveIfAbsent(id, id, 5, TimeUnit.MINUTES);

        SuccessResultAndMessage resultAndMessage = new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "사용 가능한 아이디 입니다.");
        return new ResponseEntity<>(resultAndMessage, HttpStatus.OK);
    }

    @Operation(summary = "Email Check", description = "이메일 중복 체크, 회원 등록 전 반드시 수행")
    @PostMapping("/email-check")
    public ResponseEntity<SuccessResultAndMessage> emailDuplicateCheck(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email 객체") @Validated @RequestBody SignUpEmailDto emailDto,
            @Parameter(hidden = true) BindingResult bindingResult) {

        log.debug("SignUpEmailDto={}", emailDto.toString());

        if (bindingResult.hasErrors()) {
            String defaultMessage = bindingResult.getFieldError().getDefaultMessage();
            throw new IllegalArgumentException(defaultMessage);
        }

        // 유효한 학교 email 인지 검사
        String univName = singUpService.checkValidUnivEmailAddr(emailDto);

        // email 중복 검사
        singUpService.checkDuplicateEmail(emailDto);

        String email = emailDto.getEmail();
        // redis 에 데이터 저장
        redisService.saveIfAbsent(email, email, 5, TimeUnit.MINUTES);

        SuccessResultAndMessage resultAndMessage = new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "사용 가능한 이메일 입니다.:"+univName);
        return new ResponseEntity<>(resultAndMessage, HttpStatus.OK);
    }

    @Operation(summary = "이메일 인증", description = "이메일 인증 엔드포인트, 회원가입 이후에 수행(레디스 이용-48시간 동안 유효)")
    @PatchMapping("/verify-univ-email-address")
    public ResponseEntity<EmailVerificationDto> verifyEmailAddress(@Parameter(name = "authToken") @RequestParam(name = "authToken") String authToken) {

        EmailVerificationDto verifyResult = singUpService.verify(authToken);

        log.info("user email verified, userId={}, email={}, Time={}", verifyResult.getLoginId(), verifyResult.getEmail(), new Timestamp(System.currentTimeMillis()));
        log.debug("verifyResult={}", verifyResult);

        return new ResponseEntity<>(verifyResult, HttpStatus.OK);
    }
}
