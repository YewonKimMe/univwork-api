package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentReportDto;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Comment", description = "댓글 관련 부가 기능")
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 신고", description = "특정 UUID 댓글 신고")
    @PatchMapping
    public ResponseEntity<ResultAndMessage> reportComments(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 고유 식별자(UUID)") @RequestBody final CommentReportDto dto
    ) {
        commentService.reportComment(dto.getCommentUuid());
        return ResponseEntity.ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "댓글 신고가 완료되었습니다."));
    }
}
