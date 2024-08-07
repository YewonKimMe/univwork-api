package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.*;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.domain.entity.ReportedComment;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import net.univwork.api.api_v1.enums.BlockRole;
import net.univwork.api.api_v1.service.AdminService;
import net.univwork.api.api_v1.service.SingUpService;
import net.univwork.api.api_v1.tool.WorkplaceExcelFileParser;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "Admin", description = "관리자 기능 관련 엔드포인트")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {

    private final AdminService adminService;

    private final SingUpService singUpService;

    private final WorkplaceExcelFileParser excelFileParser;

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
    public ResponseEntity<PagedModel<EntityModel<ReportedComment>>> getReportedComments(
            @RequestParam(name = "page", defaultValue = "0") final int pageNumber,
            @RequestParam(name = "size", defaultValue = "30") final int pageLimit,
            @Parameter(hidden = true) PagedResourcesAssembler<ReportedComment> assembler
    ) {
        Page<ReportedComment> reportedCommentList = adminService.getReportedCommentList(pageNumber, pageLimit);
        PagedModel<EntityModel<ReportedComment>> model = assembler.toModel(reportedCommentList);
        return ResponseEntity.ok().body(model);
    }

    @Operation(summary = "신고된 댓글 삭제", description = "신고 댓글 삭제, ADMIN")
    @DeleteMapping("/reported-comments/{commentId}")
    public ResponseEntity<ResultAndMessage> deleteReportedComment(@PathVariable(name = "commentId") String commentUuid) {

        adminService.deleteCommentToReported(commentUuid);

        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "댓글이 삭제 처리 되었습니다."));
    }

    @Operation(summary = "유저 차단", description = "유저 차단(신고된 사람 writer or 신고자 reporter), userId 로 결정, ADMIN")
    @PatchMapping("/reported-comments/users/{userId}")
    public ResponseEntity<ResultAndMessage> blockUser(@PathVariable(name = "userId") String userId,
                                                      @RequestParam(name = "commentId") String commentId,
                                                      @RequestParam(name = "reason") String reason,
                                                      @Parameter(name = "role", description = "유저 차단 시 작성자, 신고자 구별용") @RequestParam(name = "role") String role) {
        adminService.blockUser(userId, commentId, BlockRole.fromValue(role));
        log.debug("reason={}", reason);
        String target = role.equals("writer") ? "작성자" : "신고자";
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), target + " 차단 완료"));
    }

    @Operation(summary = "신고 무시", description = "신고 무시, 신고 리스트에서 제거, ADMIN")
    @PatchMapping("/reported-comments/{commentId}")
    public ResponseEntity<ResultAndMessage> dismissReport(@PathVariable(name = "commentId") String commentId) {
        log.debug("uuid={}", commentId);
        adminService.dismissReport(commentId);
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), commentId + " 의 신고가 반려되었습니다."));
    }

    @Operation(summary = "근로지 엑셀 파일 등록", description = "근로지 엑셀 파일을 등록하는 API")
    @PostMapping(value = "/workplaces/add-workplaces", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultAndMessage> addWorkplacesToExcel(
            @Parameter(
                    description = "근로지 엑셀 파일, 양식 확인 필수",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )@RequestPart("multipartFile") MultipartFile file,
            @Parameter(name = "univCode", description = "학교 코드") @RequestParam(name = "univCode") Long univCode) {
        excelFileParser.readExcel(file, univCode);
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "근로지가 등록됨"));
    }

    @Operation(summary = "근로지 개별 등록", description = "근로지 개별 등록하는 API")
    @PostMapping(value = "/workplaces/{univCode}")
    public ResponseEntity<ResultAndMessage> addWorkplace(@Parameter(name = "univCode", description = "univCode") @PathVariable(name = "univCode") Long univCode,
                                                         @RequestBody AddWorkplaceDto dto) {
        adminService.addWorkplace(univCode, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResultAndMessage(HttpStatus.CREATED.getReasonPhrase(), "근로지 추가됨"));
    }

    @Operation(summary = "유저 개별 등록", description = "유저 개별 등록하는 API")
    @PostMapping(value = "/user/added")
    public ResponseEntity<ResultAndMessage> addUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 등록 폼") @Validated @RequestBody SignUpFormDto signUpFormDto) {
        // 가입 과정에서 SignUpController 의 id, email 중복 check 필수(프론트 처리)
        adminService.addUser(signUpFormDto);

        return ResponseEntity.
                status(HttpStatus.CREATED)
                .body(new SuccessResultAndMessage(HttpStatus.CREATED.getReasonPhrase(), "유저 아이디=" + signUpFormDto.getId() + " 유저가 생성됨"));
    }

    @Operation(summary = "유저 리스트 조회", description = "전체 유저 조회 API")
    @GetMapping("/users")
    public ResponseEntity<PagedModel<EntityModel<AdminAspectUserDetailDto>>> getUserList(@RequestParam(name = "page", defaultValue = "0") final int pageNumber,
                                                        @RequestParam(name = "size", defaultValue = "45") final int pageLimit,
                                                        @RequestParam(name = "searchUsername", required = false) final String username,
                                                        @Parameter(hidden = true) PagedResourcesAssembler<AdminAspectUserDetailDto> assembler) {

        Page<AdminAspectUserDetailDto> userList = adminService.getUserList(pageNumber, pageLimit, username);
        PagedModel<EntityModel<AdminAspectUserDetailDto>> model = assembler.toModel(userList);


        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(4, TimeUnit.SECONDS))
                .body(model);
    }

    @Operation(summary = "유저 인증 처리", description = "유저 인증 완료 처리 API")
    @PatchMapping("/users/verify")
    public ResponseEntity<ResultAndMessage> verifyUserByUserEmail(@RequestBody SignUpEmailDto emailDto) {

        // verify update 처리 로직
        adminService.setUserVerified(emailDto.getEmail());

        return ResponseEntity
                .ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), emailDto.getEmail() + "유저 인증 정보가 [인증완료] 로 갱신됨."));
    }

    @Operation(summary = "유저 차단 처리", description = "유저 차단 처리 API")
    @PatchMapping("/users/block")
    public ResponseEntity<ResultAndMessage> blockUserByUserEmail(@RequestBody SignUpEmailDto emailDto) {

        // block update 처리 로직
        adminService.setUserBlocked(emailDto.getEmail());
        return ResponseEntity
                .ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), emailDto.getEmail() + "유저가 [차단] 처리됨."));
    }

    @Operation(summary = "유저 차단 해제", description = "유저 차단 해제 API")
    @PatchMapping("/users/release")
    public ResponseEntity<ResultAndMessage> releaseUserByUserEmail(@RequestBody SignUpEmailDto emailDto) {

        // block update 처리 로직
        adminService.setUserBlockedRelease(emailDto.getEmail());
        return ResponseEntity
                .ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), emailDto.getEmail() + "유저가 [차단 해제] 처리됨."));
    }

    @Operation(summary = "인증 메일 발송", description = "관리자 권한으로 인증 메일을 재발송 하는 API")
    @PostMapping(value = "/user/resend-verify-univ-email")
    public ResponseEntity<ResultAndMessage> reVerifyByAdmin(@RequestBody SignUpEmailDto emailDto) {
        adminService.sendVerifyEmail(emailDto);
        return ResponseEntity
                .ok()
                .body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "관리자 권한으로 유저 이메일 인증메일 발송됨"));
    }
}
