package net.univwork.api.api_v1.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/sign-up")
public class SignUpController {

    private final SingUpService singUpService;

    private final RedisService redisService;

    @PostMapping("/register")
    public SuccessResultAndMessage signUp(
            @Validated @RequestBody SignUpFormDto signUpFormDto
            ) {
        return null;
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

        SuccessResultAndMessage resultAndMessage = new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "사용 가능한 이메일 입니다.");
        return new ResponseEntity<>(resultAndMessage, HttpStatus.OK);
    }

    @GetMapping("/verify-email-address")
    public String verifyEmailAddress(@RequestParam(name = "auth-token") String authToken) {
        // redisService 에서 authToken 으로 이메일을 찾아오고

        // 찾은 이메일로 User 의 verify = 1 설정 후, User 획득

        // 획득한 User 의 로그인 아이디와 이메일을 dto에 매핑 후 model에 담고 view 이름 반환
        return null;
    }
}
