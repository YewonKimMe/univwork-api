package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentReportDto;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Comment", description = "댓글 관련 부가 기능 엔드포인트")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/comment", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 신고", description = "특정 UUID 댓글 신고")
    @PatchMapping("/{comment-uuid}")
    public ResponseEntity<ResultAndMessage> reportComments(
            @Parameter(name = "comment-uuid", description = "댓글 UUID", in = ParameterIn.PATH) @PathVariable(name = "comment-uuid") final String commentUuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 고유 식별자(UUID), 사유(REASON_욕설, 개인정보유포, 명예훼손, 사칭, 광고) 중 하나, 정확하게 입력하여야 함.") @RequestBody final CommentReportDto dto,
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) Authentication authentication
    ) {
        if (!commentUuid.equals(dto.getCommentUuid())) {
            log.debug("UUID 불일치");
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        commentService.reportComment(dto, request, authentication);
        return ResponseEntity.ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "댓글 신고가 완료되었습니다."));
    }
}
