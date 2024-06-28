package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.dto.PasswordChangeDto;
import net.univwork.api.api_v1.domain.dto.UserDetailDto;
import net.univwork.api.api_v1.domain.response.ErrorResultAndMessage;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.exception.PasswordNotMatchException;
import net.univwork.api.api_v1.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "UserFeature", description = "로그인 유저 기능 관련 엔드 포인트")
@RestController
@RequestMapping(value = "/api/v1/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserFeatureController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDetailDto> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authentication.getName();
        UserDetailDto userDetail = userService.findUserById(userId);
        return ResponseEntity.ok().body(userDetail);
    }

    @GetMapping("/my-comments")
    public ResponseEntity<Page<CommentDto>> getUserComments(Authentication authentication) {
        return null;
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ResultAndMessage> changePwd(
            @RequestBody PasswordChangeDto passwordChangeDto,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) { // 인증 X
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!passwordChangeDto.getNewPwd().equals(passwordChangeDto.getNewPwdCheck())) { // 비밀번호 확인 일치 X
            throw new PasswordNotMatchException("비밀번호 확인이 일치하지 않습니다.");
        }

        int changeRow = userService.updateUserPassword(authentication.getName(), passwordChangeDto.getCurrentPwd(), passwordChangeDto.getNewPwd());

        if (changeRow == -1) { // 기존 비밀번호와 새 비밀번호가 동일한 경우
            return ResponseEntity.badRequest().body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), "기존 비밀번호와 새 비밀번호가 동일합니다."));
        }

        if (changeRow == 1) { // 정상적으로 변경되어 변경된 행이 1일 경우
            return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "비밀번호가 변경되었습니다."));
        }
        throw new BadCredentialsException("유저 인증정보가 올바르지 않습니다."); // 유저가 존재하지 않는 경우, 변경된 row 가 0
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<SuccessResultAndMessage> deleteUser(Authentication authentication) {
        return null;
    }
}
