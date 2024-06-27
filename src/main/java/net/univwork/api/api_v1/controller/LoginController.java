package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Login", description = "Basic Auth Login")
@RequestMapping("/api/v1/login")
public class LoginController {

    @Operation(summary = "Login", description = "Basic Authentication Login end-point")
    @PostMapping
    public ResponseEntity<SuccessResultAndMessage> login(Authentication authentication) {
        if (authentication != null) {
            return ResponseEntity.ok(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "로그인 되었습니다."));
        }
        throw new BadCredentialsException("로그인 과정에서 오류가 발생했습니다.");
    }
}
