package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.dto.PasswordChangeDto;
import net.univwork.api.api_v1.domain.dto.PasswordDto;
import net.univwork.api.api_v1.domain.dto.UserDetailDto;
import net.univwork.api.api_v1.domain.response.ErrorResultAndMessage;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.exception.DuplicationException;
import net.univwork.api.api_v1.exception.PasswordNotMatchException;
import net.univwork.api.api_v1.exception.UserNotExistException;
import net.univwork.api.api_v1.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "UserFeature", description = "로그인 유저 기능 관련 엔드 포인트")
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserFeatureController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회", description = "유저 회원 정보 조회")
    @GetMapping
    public ResponseEntity<UserDetailDto> getUserInfo(@Parameter(hidden = true) Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authentication.getName();
        UserDetailDto userDetail = userService.findUserById(userId);
        return ResponseEntity.ok().body(userDetail);
    }

    @Operation(summary = "댓글 조회", description = "특정 회원 댓글 조회")
    @GetMapping("/my-comments")
    public ResponseEntity<PagedModel<EntityModel<CommentDto>>> getUserComments(
            @RequestParam(name = "page", defaultValue = "0") final int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") final int pageLimit,
            @Parameter(hidden = true) Authentication authentication, @Parameter(hidden = true) PagedResourcesAssembler<CommentDto> assembler) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<CommentDto> userCommentList = userService.getCommentsPerUser(pageNumber, pageLimit, authentication);
        PagedModel<EntityModel<CommentDto>> model = assembler.toModel(userCommentList);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(model);
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경")
    @PatchMapping("/change-password")
    public ResponseEntity<ResultAndMessage> changePwd(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "비밀번호 변경 객체") @RequestBody PasswordChangeDto passwordChangeDto,
            @Parameter(hidden = true) Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) { // 인증 X
            log.debug("인증 미존재");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResultAndMessage(HttpStatus.UNAUTHORIZED.getReasonPhrase(), "인증 정보가 존재하지 않습니다."));
        }

        if (!passwordChangeDto.getNewPwd().equals(passwordChangeDto.getNewPwdCheck())) { // 비밀번호 확인 일치 X
            log.debug("비밀번호 확인 일치 X");
            throw new PasswordNotMatchException("비밀번호 확인이 일치하지 않습니다.");
        }

        int changeRow = userService.updateUserPassword(authentication.getName(), passwordChangeDto.getCurrentPwd(), passwordChangeDto.getNewPwd());

        if (changeRow == -1) { // 기존 비밀번호와 새 비밀번호가 동일한 경우
            log.debug("기존 비밀번호 == 새 비밀번호");
            throw new DuplicationException("기존 비밀번호와 새 비밀번호가 동일합니다.");
            //return ResponseEntity.badRequest().body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), "기존 비밀번호와 새 비밀번호가 동일합니다."));
        }

        if (changeRow == 1) { // 정상적으로 변경되어 변경된 행이 1일 경우
            log.debug("비밀번호 변경 완료");
            return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "비밀번호가 변경되었습니다."));
        }
        throw new BadCredentialsException("유저 인증정보가 올바르지 않아 비밀번호를 변경하지 못했습니다."); // 유저가 존재하지 않는 경우, 변경된 row 가 0
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴(Hard Delete)")
    @DeleteMapping("/withdrawal")
    public ResponseEntity<ResultAndMessage> deleteUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "비밀번호 객체") @Validated @RequestBody PasswordDto passwordDto,
            @Parameter(hidden = true) BindingResult bindingResult,
            @Parameter(hidden = true) Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (bindingResult.hasFieldErrors()) {
            String fieldErrorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ErrorResultAndMessage(HttpStatus.BAD_REQUEST.getReasonPhrase(), fieldErrorMessage));
        }
        int changeRow = userService.withdraw(authentication.getName(), passwordDto.getPassword());
        if (changeRow == 1) {
            return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "계정이 삭제되었습니다."));
        }
        throw new UserNotExistException("인증 정보가 유효하지 않거나, 이미 삭제된 계정입니다.");
    }
}
