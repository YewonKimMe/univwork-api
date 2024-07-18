package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.EmailDto;
import net.univwork.api.api_v1.domain.dto.PasswordFindDto;
import net.univwork.api.api_v1.domain.response.ErrorResultAndMessage;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.service.FindPasswordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/find-password", produces = MediaType.APPLICATION_JSON_VALUE)
public class FindPasswordController {

    private final FindPasswordService findPasswordService;

    @Operation(summary = "비밀번호 찾기 이메일 발송", description = "비밀번호 찾기 이메일 발송, 시간은 5분")
    @PostMapping("/password-email")
    public ResponseEntity<ResultAndMessage> sendMessage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "이메일 JSON") @Validated @RequestBody EmailDto email,
            BindingResult bindingResult
            ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), Objects.requireNonNull(bindingResult.getFieldError("email")).getDefaultMessage()));
        }
        findPasswordService.sendFindPasswordEmail(email.getEmail());
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "비밀번호 변경 이메일이\n" + email.getEmail() + " 으로 발송 되었습니다."));
    }

    @Operation(summary = "인증 토큰 검증", description = "인증 토큰 검증 및 이메일 획득")
    @GetMapping("/auth-token-and-email")
    public ResponseEntity<ResultAndMessage> verifyAuthTokenAndGetEmail(@RequestParam(name = "authToken") String authToken) {
        String email = findPasswordService.checkAuthTokenAndReturnEmail(authToken);
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), email));
    }

    @Operation(summary = "비밀번호 변경", description = "이메일 속 링크로 비밀번호 변경")
    @PostMapping("/password")
    public ResponseEntity<ResultAndMessage> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "패스워드 변경 객체, authToken + pwd") @Validated @RequestBody PasswordFindDto passwordFindDto,
            BindingResult bindingResult
            ) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.joining("\n"));
            return ResponseEntity.badRequest().body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMessage)); // 나중에 stream으로 객체 매핑
        }

        // 이메일, AuthToken 검증 후 Email 으로 User 검색, Password 변경
        findPasswordService.changePassword(passwordFindDto.getAuthToken(), passwordFindDto.getPassword());
        return ResponseEntity.ok().body(new ErrorResultAndMessage(HttpStatus.OK.getReasonPhrase(), "비밀번호가 변경되었습니다. 변경된 비밀번호로 로그인 해 주세요."));
    }
}
