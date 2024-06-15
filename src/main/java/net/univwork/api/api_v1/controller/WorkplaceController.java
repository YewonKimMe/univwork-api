package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.dto.CommentFormDto;
import net.univwork.api.api_v1.domain.dto.WorkplaceDetailDto;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.enums.CookieName;
import net.univwork.api.api_v1.service.WorkplaceService;
import net.univwork.api.api_v1.tool.CookieUtils;
import net.univwork.api.api_v1.tool.IpTool;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Workplace", description = "근로지 관련 기능")
@RestController
@RequestMapping(value = "/api/v1/universities/{univ-code}/workplaces/{workplace-code}", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkplaceController {

    private final WorkplaceService service; // 서비스 객체

    /**
     * getWorkplaceDetail: 근로지 정보와 근로지 댓글을 가져옴
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @param pageNumber 페이지 번호
     * @param pageLimit 페이지 당 요소 수
     * @see net.univwork.api.api_v1.service.WorkplaceService#getWorkplace(Long, Long)
     * @see net.univwork.api.api_v1.service.WorkplaceService#getWorkplaceComments(Long, Long, int, int)
     * @see net.univwork.api.api_v1.domain.dto.WorkplaceDetailDto
     * WorkplaceDetailDto: 응답 리턴 복합 객체(workplace, commentPage)
     * @since 1.0.0
     * @return WorkplaceDetaiolDto: [Workplace + Page&lt;CommentDto&gt;]<br>근로지 정보와 댓글을 복합객체로 리턴
     * */
    @Operation(summary = "근로지 정보 획득", description = "근로지 정보와 근로지 댓글을 최신순으로 가져오는 API")
    @Parameters({
            @Parameter(name = "univ-code", description = "학교 코드", in = ParameterIn.PATH),
            @Parameter(name = "workplace-code", description = "근로지 코드", in = ParameterIn.PATH),
            @Parameter(name = "page-number", description = "페이지 번호, 기본 0", in = ParameterIn.QUERY),
            @Parameter(name = "page-limit", description = "페이지 당 댓글 갯수, 기본 10", in = ParameterIn.QUERY)
    })
    @GetMapping
    public ResponseEntity<WorkplaceDetailDto> getWorkplaceDetail(
            @PathVariable(name = "univ-code") final Long univCode,
            @PathVariable(name = "workplace-code") final Long workplaceCode,
            @RequestParam(name = "page-number", defaultValue = "0") final int pageNumber,
            @RequestParam(name = "page-limit", defaultValue = "10") final int pageLimit) {

        // 근로지 조회수 1 증가
        service.updateView(univCode, workplaceCode);

        // 근로지 획득
        Workplace workplace = service.getWorkplace(univCode, workplaceCode);

        // 근로지 댓글 페이지 획득
        Page<CommentDto> workplaceComments = service.getWorkplaceComments(univCode, workplaceCode, pageNumber, pageLimit);

        // 복합 객체 생성
        WorkplaceDetailDto workplaceDetailDto = new WorkplaceDetailDto(workplace, workplaceComments);

        return new ResponseEntity<>(workplaceDetailDto, HttpStatus.OK);
    }

    /**
     * saveWorkplaceComment: 근로지에 댓글을 추가하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @see IpTool#getIpAddr()
     * @see net.univwork.api.api_v1.tool.CookieUtils#getUserCookie(HttpServletRequest, CookieName)
     * @see net.univwork.api.api_v1.service.WorkplaceService#saveWorkplaceComment(CommentFormDto, Long, Long, String, String)
     * @since 1.0.0
     * @return CommentDto
     * */
    @Operation(summary = "근로지 댓글 등록", description = "근로지 댓글 입력값 유효성 검사 및 등록 처리")
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
            HttpServletRequest request) {

        if (bindingResult.hasErrors()) { // binding 결과에 오류가 있을 경우
            StringBuilder errorMessage = new StringBuilder(); // 오류 메세지 StringBuilder 생성
            List<FieldError> fieldErrors = bindingResult.getFieldErrors(); // 필드 에러를 가져와서

            for (FieldError fieldError : fieldErrors) {
                errorMessage.append(fieldError.getDefaultMessage()).append(";"); // 오류메세지에 더함
            }
            log.debug(bindingResult.getFieldError().getDefaultMessage());

            throw new IllegalArgumentException(String.valueOf(errorMessage)); // IllgalArg 예외 던짐
        }

        // PathVariable 과 CommentDto 의 univCode, workplaceCode 가 같지 않은 경우, 조작된 상황
        if (!univCode.equals(comment.getUnivCode()) || !workplaceCode.equals(comment.getWorkplaceCode())) {
            throw new IllegalArgumentException("근로지 댓글 등록 과정에서 오류가 발생하였습니다.");
        }
        // 정상 처리 로직
        // 작성자 IP 획득
        String ipAddr = IpTool.getIpAddr(request);

        // 유저 쿠키 정보 획득
        String userCookie = CookieUtils.getUserCookie(request, CookieName.USER_COOKIE);

        // 댓글 저장 후, 리턴하기 위해 CommentDto 형식으로 반환
        CommentDto commentDto = service.saveWorkplaceComment(comment, univCode, workplaceCode, ipAddr, userCookie);

        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }
}
