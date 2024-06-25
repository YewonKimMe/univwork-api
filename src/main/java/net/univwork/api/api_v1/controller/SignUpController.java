package net.univwork.api.api_v1.controller;

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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/sign-up")
public class SignUpController {

    private final SingUpService singUpService;

    private final RedisService redisService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResultAndMessage> signUp(
            @Validated @RequestBody SignUpFormDto signUpFormDto,
            BindingResult bindingResult) {
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
            throw new IllegalArgumentException("아이디가 검증된 입력값과 일치하지 않습니다.");
        }
        if (null == findUserEmail) {
            throw new IllegalArgumentException("이메일이 검증된 입력값과 일치하지 않습니다.");
        }

        singUpService.createUser(signUpFormDto);
        SuccessResultAndMessage result = new SuccessResultAndMessage(HttpStatus.CREATED.getReasonPhrase(), "계정이 생성되었습니다.");
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/id-check")
    public ResponseEntity<SuccessResultAndMessage> idDuplicateCheck(
            @Validated @RequestBody SignUpIdDto idDto,
            BindingResult bindingResult
    ) {
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

    @PostMapping("/email-check")
    public ResponseEntity<SuccessResultAndMessage> emailDuplicateCheck(
            @Validated @RequestBody SignUpEmailDto emailDto,
            BindingResult bindingResult
    ) {
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

    @GetMapping("/verify-univ-email-address")
    public ResponseEntity<EmailVerificationDto> verifyEmailAddress(@RequestParam(name = "auth-token") String authToken) {
        // redisService 에서 authToken 으로 이메일을 찾아오고
        String email = redisService.find(authToken);
        if (email == null) {
            String errorMessage = "Authentication Token 이 유효하지 않습니다.";

            return null;
        }
        // 찾은 이메일로 User 의 verify = 1 설정 후, User 획득

        // 획득한 User 의 로그인 아이디와 이메일을 dto에 매핑 후, redisService 에서 key 삭제

        // 반환
        return null;
    }
}
