package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.NoticeTitleDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.service.NoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "공지사항", description = "공지사항 관련 기능 엔드포인트")
@RequestMapping(value = "/api/v1/notices", produces = MediaType.APPLICATION_JSON_VALUE)
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 리스트 조회", description = "공지사항 리스트 조회")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<NoticeTitleDto>>> getNotices(
            @RequestParam(name = "page", defaultValue = "0") final int pageNumber,
            @RequestParam(name = "size", defaultValue = "20") final int pageLimit,
            @Parameter(hidden = true) PagedResourcesAssembler<NoticeTitleDto> assembler) {

        Page<NoticeTitleDto> notices = noticeService.getNotices(pageNumber, pageLimit);

        PagedModel<EntityModel<NoticeTitleDto>> model = assembler.toModel(notices);

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(model);
    }

    @Operation(summary = "공지사항 조회", description = "공지사항 세부 조회")
    @GetMapping("/{noticeNo}")
    public ResponseEntity<Notice> getNoticeDetail(@Parameter(name = "noticeNo", description = "공지 번호", in = ParameterIn.PATH) @PathVariable(name = "noticeNo") Long noticeNo) {

        Notice noticeDetail = noticeService.getNoticeDetail(noticeNo);

        return ResponseEntity.ok().cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(noticeDetail);
    }
}
