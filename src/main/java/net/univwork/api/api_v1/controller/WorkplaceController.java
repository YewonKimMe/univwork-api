package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.dto.CommentFormDto;
import net.univwork.api.api_v1.domain.dto.WorkplaceDetailDto;
import net.univwork.api.api_v1.domain.dto.WorkplaceRatingDto;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.service.WorkplaceService;
import net.univwork.api.api_v1.tool.IpTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Workplace", description = "근로지 관련 기능")
@RestController
@RequestMapping(value = "/api/v1/universities/{univ-code}/workplaces/{workplace-code}", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkplaceController {

    private final WorkplaceService service;

    @Value("${comment.isAllowAnonymousUsers:true}")
    private boolean isAllowAnonymousUsers;

    /**
     * getWorkplaceDetail: 근로지 정보와 근로지 댓글을 가져옴
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @param pageNumber 페이지 번호
     * @param pageLimit 페이지 당 요소 수
     * @see net.univwork.api.api_v1.service.WorkplaceService#getWorkplace(Long, Long)
     * @see net.univwork.api.api_v1.service.WorkplaceService#getWorkplaceComments(Long, Long, int, int, Authentication, boolean)
     * @see net.univwork.api.api_v1.domain.dto.WorkplaceDetailDto
     * WorkplaceDetailDto: 응답 리턴 복합 객체(workplace, commentPage)
     * @since 1.0.0
     * @return WorkplaceDetaiolDto: [Workplace + Page&lt;CommentDto&gt;]<br>근로지 정보와 댓글을 복합객체로 리턴
     * */
    @Operation(summary = "근로지 정보 획득", description = "근로지 정보와 근로지 댓글을 최신순으로 가져오는 엔드포인트")
    @Parameters({
            @Parameter(name = "univ-code", description = "학교 코드", in = ParameterIn.PATH),
            @Parameter(name = "workplace-code", description = "근로지 코드", in = ParameterIn.PATH),
            @Parameter(name = "page", description = "페이지 번호, 기본 0", in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "페이지 당 댓글 갯수, 기본 10", in = ParameterIn.QUERY)
    })
    @GetMapping
    public ResponseEntity<WorkplaceDetailDto> getWorkplaceDetail(
            @PathVariable(name = "univ-code") final Long univCode,
            @PathVariable(name = "workplace-code") final Long workplaceCode,
            @RequestParam(name = "page", defaultValue = "0") final int pageNumber,
            @RequestParam(name = "size", defaultValue = "10") final int pageLimit,
            @Parameter(hidden = true) Authentication authentication,
            @Parameter(hidden = true) PagedResourcesAssembler<CommentDto> assembler) {

        // 근로지 조회수 1 증가
        service.updateView(univCode, workplaceCode);

        // 근로지 획득
        Workplace workplace = service.getWorkplace(univCode, workplaceCode);

        // 근로지 댓글 페이지 획득
        Page<CommentDto> workplaceComments = service.getWorkplaceComments(univCode, workplaceCode, pageNumber, pageLimit, authentication, isAllowAnonymousUsers);

        // 근로지 rating 획득
        WorkplaceRatingDto workplaceRatingDto = service.calculateRatingResult(univCode, workplaceCode);

        // PagedModel
        PagedModel<EntityModel<CommentDto>> model = assembler.toModel(workplaceComments);

        // 복합 객체 생성
        WorkplaceDetailDto workplaceDetailDto = new WorkplaceDetailDto(workplace, workplaceRatingDto, model);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(4, TimeUnit.SECONDS))
                .body(workplaceDetailDto);
    }

    /**
     * saveWorkplaceComment: 근로지에 댓글을 추가하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @see IpTool#getIpAddr()
     * @see net.univwork.api.api_v1.tool.CookieUtils#getUserCookie(HttpServletRequest, CookieName)
     * @see net.univwork.api.api_v1.service.WorkplaceService#saveWorkplaceComment(CommentFormDto, Long, Long, Authentication, boolean, HttpServletRequest, HttpServletResponse)
     * @since 1.0.0
     * @return CommentDto
     * */
    @Operation(summary = "근로지 댓글 등록", description = "근로지 댓글 입력값 유효성 검사 및 등록을 처리하는 엔드포인트", security = {@SecurityRequirement(name = "bearerAuth")})
    @Parameters({
            @Parameter(name = "univ-code", description = "학교 코드", in = ParameterIn.PATH),
            @Parameter(name = "workplace-code", description = "근로지 코드", in = ParameterIn.PATH)
    })
    @PostMapping("/comments")
    public ResponseEntity<CommentDto> saveWorkplaceComment (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 Comment 요청 본문") @Validated @RequestBody CommentFormDto comment,
            BindingResult bindingResult,
            @PathVariable(name = "univ-code") final Long univCode,
            @PathVariable(name = "workplace-code") final Long workplaceCode,
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {


        if (bindingResult.hasErrors()) { // binding 결과에 오류가 있을 경우
            StringBuilder errorMessage = new StringBuilder(); // 오류 메세지 StringBuilder 생성
            List<FieldError> fieldErrors = bindingResult.getFieldErrors(); // 필드 에러를 가져와서

            for (FieldError fieldError : fieldErrors) {
                errorMessage.append(fieldError.getDefaultMessage()).append(";"); // 오류메세지에 더함
            }
            log.debug(bindingResult.getFieldError().getDefaultMessage());

            throw new IllegalArgumentException(String.valueOf(errorMessage)); // IllgalArg 예외 던짐
        }

        if (!univCode.equals(comment.getUnivCode()) || !workplaceCode.equals(comment.getWorkplaceCode())) { // PathVariable 과 CommentDto 의 univCode, workplaceCode 가 같지 않은 경우, 조작된 상황
            throw new IllegalArgumentException("근로지 댓글 등록 과정에서 오류가 발생하였습니다.");
        }

        // ---------- 정상 처리 로직 ------------

        CommentDto commentDto = service.saveWorkplaceComment(comment, univCode, workplaceCode, authentication, isAllowAnonymousUsers, request, response); // 댓글 저장 후, 리턴하기 위해 CommentDto 형식으로 반환

        log.debug("Saved And return CommentDto={}", commentDto);

        return ResponseEntity.ok(commentDto);
    }
}
