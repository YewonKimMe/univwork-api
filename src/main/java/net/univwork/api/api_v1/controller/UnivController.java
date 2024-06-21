package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.University;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.enums.SortOption;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.service.UnivService;
import net.univwork.api.api_v1.tool.ConstString;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "University", description = "학교 관련 기능")
@RestController
@RequestMapping(value = "/api/v1/universities", produces = MediaType.APPLICATION_JSON_VALUE)
public class UnivController {

    private final UnivService univService; // 서비스 객체

    /**
     * getAllUniversities: 학교 리스트 조회+정렬+검색 컨트롤러
     * @param pageNumber 페이지 번호,
     * @param pageLimit 페이지 당 요소 갯수,
     * @param universitySearchKeyword (선택) 학교 이름,
     * @param sortParam 정렬 옵션: 학교명 오름차순, 학교명 내림차순
     * @apiNote 학교 리스트를 조회, 학교명 세부검색 및 학교이름 오름/내림차순 정렬 기능 보유
     * @since 1.0.0
     * @see net.univwork.api.api_v1.service.UnivService#getUniversities(int, int, String, SortOption) 
     * */
    @Operation(summary = "학교 리스트 조회", description = "근로 참여 학교 리스트 조회, 검색 및 정렬 기능 포함")
    @Parameters({
            @Parameter(name = "page-number", description = "페이지 숫자, 기본 0, 만약 univName 으로 검색할 경우 기본값으로 유지", in = ParameterIn.QUERY),
            @Parameter(name = "page-limit", description = "한 페이지 당 요소 갯수, 기본 30", in = ParameterIn.QUERY),
            @Parameter(name = "university-name", description = "검색 학교명(선택)", in = ParameterIn.QUERY),
            @Parameter(name = "sort", description = "정렬 옵션<br>univAsc_학교명 오름차순<br>univDesc_학교명 내림차순", in = ParameterIn.QUERY)
    })
    @GetMapping
    public ResponseEntity<Page<University>> getAllUniversities(
            @RequestParam(name = "page-number", defaultValue = "0") final int pageNumber,
            @RequestParam(name = "page-limit", defaultValue = "30") final int pageLimit,
            @RequestParam(name = "university-name", required = false) final String universitySearchKeyword,
            @RequestParam(name = "sort", defaultValue = ConstString.UNIV_NAME_ASC) final String sortParam) {

        // enum 획득
        SortOption sortOption = SortOption.fromValue(sortParam);

        // 학교 페이지 + 조건 조회
        Page<University> universityPage = univService.getUniversities(pageNumber, pageLimit, universitySearchKeyword, sortOption);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .body(universityPage);
    }

    /**
     * getWorkplaces: 학교 별 근로지 리스트 조회 + 검색 + 정렬 메소드
     * @param univCode 학교 코드(정수),
     * @param pageNumber 페이지 번호,
     * @param pageLimit 페이지 당 요소 갯수,
     * @param workplaceSearchKeyword (선택) 근로지 이름 검색어,
     * @param sortParam 정렬 옵션: 근로지명 오름차순, 근로지명 내림차순, 조회수 오름차순, 조회수 내림차순, 댓글 오름차순, 댓글 내림차순
     * @apiNote 학교 코드가 일치하는 근로지를 획득, 학교 이름으로 세부 검색 및 근로지 이름, 댓글수, 조회수 등으로 오름/내림차순 가능
     * @since 1.0.0
     * @see net.univwork.api.api_v1.service.UnivService#getWorkplaces(Long, int, int, String, WorkplaceType, SortOption)
     * */
    @Operation(summary = "학교 별 근로지 조회", description = "학교별 근로지 조회, 정렬 및 검색 기능 포함")
    @Parameters({
            @Parameter(name = "univ-code", description = "학교 코드(정수)", in = ParameterIn.PATH),
            @Parameter(name = "page-number", description = "페이지 숫자, 기본 0, 만약 workplace 으로 검색할 경우 기본값 으로 유지", in = ParameterIn.QUERY),
            @Parameter(name = "page-limit", description = "한 페이지 당 요소 갯수, 기본 40", in = ParameterIn.QUERY),
            @Parameter(name = "workplace-name", description = "검색 근로지명(선택)", in = ParameterIn.QUERY),
            @Parameter(name = "workplace-type", description = "근로지 종류(선택), 선택 X 시 교내, 교외 둘 다 획득<br>in 교내근로<br>out 교외근로", in = ParameterIn.QUERY),
            @Parameter(name = "sort", description = "정렬 옵션<br>workplaceAsc_근로지명 오름차순<br>workplaceDesc_근로지명 내림차순<br>workplaceViewAsc_근로지 조회수 오름차순<br>workplaceViewDesc_근로지 조회수 내림차순<br>workplaceCommentNumAsc_근로지 댓글 오름차순<br>workplaceCommentNumDesc_근로지 댓글 내림차순", in = ParameterIn.QUERY)
    })
    @GetMapping("/{univ-code}/workplaces")
    public ResponseEntity<PagedModel<Workplace>> getWorkplaces(
            @PathVariable(name = "univ-code") final Long univCode,
            @RequestParam(name = "page-number", defaultValue = "0") final int pageNumber,
            @RequestParam(name = "page-limit", defaultValue = "40") final int pageLimit,
            @RequestParam(name = "workplace-name", required = false) final String workplaceSearchKeyword,
            @RequestParam(name = "workplace-type", required = false) final String workplaceTypeParam,
            @RequestParam(name = "sort", defaultValue = ConstString.WORKPLACE_NAME_ASC) final String sortParam,
            @Parameter(hidden = true) PagedResourcesAssembler assembler) {

        // 정렬 옵션 enum 획득
        SortOption sortOption = SortOption.fromValue(sortParam);

        // 근로지 종류 enum 획득(선택), null이 들어올 경우 ALL 획득
        WorkplaceType workplaceType = WorkplaceType.fromValue(workplaceTypeParam);

        // 학교 별 근로지 페이지 + 조건 조회
        Page<Workplace> workplacesPage = univService.getWorkplaces(univCode, pageNumber, pageLimit, workplaceSearchKeyword, workplaceType, sortOption);
        PagedModel model = assembler.toModel(workplacesPage);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
                .body(model);
    }
}
