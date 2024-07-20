package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.NoticeAdminDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "Admin", description = "관리자 기능 관련 엔드포인트")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "관리자 권한 확인", description = "최초 접속 시 관리자 권한 확인, ADMIN")
    @GetMapping
    public ResponseEntity<ResultAndMessage> checkAdminAuth(Authentication authentication) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedTimestamp = dateFormat.format(new Timestamp(System.currentTimeMillis()));

        log.info("[관리자 페이지 접속] 아이디: {}, 권한: {}", authentication.getName(), authentication.getAuthorities());
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "[관리자 페이지 접속]\n" + authentication.getName() + ", 접속시간: " + formattedTimestamp));
    }

    @Operation(summary = "공지사항 리스트 조회", description = "리스트 조회, ADMIN")
    @GetMapping("/notices")
    public ResponseEntity<PagedModel<EntityModel<Notice>>> getNoticeList(@RequestParam(name = "page", defaultValue = "0") final int pageNumber,
                                      @RequestParam(name = "size", defaultValue = "10") final int pageLimit,
                                      @Parameter(hidden = true) PagedResourcesAssembler<Notice> assembler) {
        Page<Notice> noticePage = adminService.getNoticeList(pageNumber, pageLimit);
        // PagedModel
        PagedModel<EntityModel<Notice>> model = assembler.toModel(noticePage);

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(2, TimeUnit.SECONDS))
                .body(model);
    }

    @Operation(summary = "공지사항 작성", description = "공지사항 최초 작성, ADMIN")
    @PostMapping("/notices")
    public ResponseEntity<ResultAndMessage> writeNotice(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 객체") @Validated @RequestBody NoticeAdminDto noticeAdminDto,
                                                        Authentication authentication) {
        adminService.saveNotice(noticeAdminDto, authentication);
        return new ResponseEntity<>(new SuccessResultAndMessage(HttpStatus.CREATED.getReasonPhrase(), "공지사항이 작성되었습니다."), HttpStatus.CREATED);
    }

    @Operation(summary = "공지사항 획득", description = "특정 공지사항 획득, 수정 목적, ADMIN")
    @GetMapping("/notices/{no}")
    public ResponseEntity<NoticeAdminDto> getNoticeForEdit(@PathVariable(name = "no") Long no) {
        Notice notice = adminService.getNotice(no);
        NoticeAdminDto noticeAdminDto = new NoticeAdminDto(notice);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(noticeAdminDto);
    }

    @Operation(summary = "공지사항 수정 후 저장", description = "공지사항 수정 후 업데이트, ADMIN")
    @PutMapping("/notices/{no}")
    public ResponseEntity<ResultAndMessage> saveNoticeEdit(@PathVariable(name = "no") Long no, @Validated @RequestBody NoticeAdminDto noticeAdminDto,
                                                           Authentication authentication) {
        adminService.editAndSaveNotice(noticeAdminDto, no, authentication);
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "공지사항이 수정 되었습니다."));
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항 삭제, ADMIN")
    @DeleteMapping("/notices/{no}")
    public ResponseEntity<ResultAndMessage> deleteNotice(@PathVariable(name = "no") Long no,
                                                         Authentication authentication) {
        adminService.deleteNotice(no, authentication);
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "공지사항이 삭제 되었습니다."));
    }

    @Operation(summary = "신고 댓글 조회", description = "신고 댓글 전체 조회, ADMIN")
    @GetMapping("/reported-comments")
    public ResponseEntity<Object> getReportedComments() {
        return null;
    }

    @Operation(summary = "신고된 댓글 삭제", description = "신고 댓글 삭제, ADMIN")
    @DeleteMapping("/reported-comments/{commentId}")
    public ResponseEntity<ResultAndMessage> deleteReportedComment(@PathVariable(name = "commentId") String parameter) {
        return null;
    }

    @Operation(summary = "유저 차단", description = "유저 차단(신고된 사람 or 신고자), userId 로 결정, ADMIN")
    @PostMapping("/reported-comments/{userId}")
    public ResponseEntity<ResultAndMessage> blockUser(@PathVariable(name = "userId") String userId) {
        return null;
    }

    @Operation(summary = "신고 무시", description = "신고 무시, 신고 리스트에서 제거, ADMIN")
    @PatchMapping("/reported-comments/{reportedId}")
    public ResponseEntity<ResultAndMessage> dismissReport(@PathVariable(name = "reportedId") String reportedId) {
        return null;
    }

}
