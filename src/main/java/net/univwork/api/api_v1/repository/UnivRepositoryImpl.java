package net.univwork.api.api_v1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.WorkplaceSummaryDto;
import net.univwork.api.api_v1.domain.entity.QUniversity;
import net.univwork.api.api_v1.domain.entity.QWorkplace;
import net.univwork.api.api_v1.domain.entity.University;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.enums.SortOption;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.tool.ConstString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Repository
public class UnivRepositoryImpl implements UnivRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public UnivRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 모든 대학교를 가져오는데, univName과 sortOption에 따라 정렬된 결과를 가져오는 함수
     * @param pageable pageable 객체(서비스 계층에서 구성)
     * @param univName @Nullable 학교명(검색 옵션)
     * @param sortOption 정렬 조건 enum(asc, desc)
     * @return Page&lt;University&gt; University 페이지 객체
     * @since 1.0.0
     * */
    @Override
    public Page<University> getUniversities(Pageable pageable, @Nullable final String univName, final SortOption sortOption) {

        QUniversity university = QUniversity.university;
        BooleanBuilder builder = new BooleanBuilder(); // 조건
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        // 학교명 검색어 있을 경우
        if (StringUtils.hasText(univName)) {
            builder.and(university.univName.like("%" + univName + "%"));
        }

        // 근로지 갯수가 0개보다 많은 경우에만 가져옴
        builder.and(university.workplaceNum.gt(1));

        // 정렬 옵션 지정
        if (sortOption == SortOption.UNIV_NAME_ASC) {
            orderSpecifier = university.univName.asc();
        } else if (sortOption == SortOption.UNIV_NAME_DESC) {
            orderSpecifier = university.univName.desc();
        }

        List<University> universities = queryFactory
                .select(university)
                .from(university)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset()) // 오프셋 설정, 몇개씩 넘어가는지
                .limit(pageable.getPageSize()) // 페이지 당 요소 갯수 설정
                .fetch();

        // 조건에 맞는 결과에 해당하는 총 university 갯수를 가져오기 위한 쿼리: SELECT count(*)
        JPAQuery<Long> countQuery = queryFactory
                .select(university.count())
                .from(university)
                .where(builder);

        // PageableExecutionUtils.getPage(): 페이지 사이즈보다 작을 경우 또는 마지막 페이지일 경우에는 추가적인 데이터베이스 쿼리를 실행하지 않음
        return PageableExecutionUtils.getPage(universities, pageable, countQuery::fetchOne);

    }

    /**
     * 대학 별 근로지를 가져오는 메소드, workplaceName과 sortOption에 따라 검색 및 정렬 가능한 메소드
     * @param pageable pageable 객체(서비스 계층에서 구현)
     * @param univCode 학교 코드
     * @param workplaceName @Nullable 근로지명(검색 옵션)
     * @param sortOption 정렬 조건 enum
     * @return Workplace 페이지 객체
     * @since 1.0.0
     * */
    @Override
    public Page<Workplace> getWorkplaces(Pageable pageable, final Long univCode, @Nullable final String workplaceName, final WorkplaceType workplaceType, final SortOption sortOption) {

        QWorkplace workplace = QWorkplace.workplace;
        BooleanBuilder builder = new BooleanBuilder(); // 조건
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        // 근로지명 검색어가 존재할 경우
        if (StringUtils.hasText(workplaceName)) {
            builder.and(workplace.workplaceName.like("%" + workplaceName + "%"));
        }

        // 학교 코드 where 조건 설정
        builder.and(workplace.univCode.eq(univCode));

        // 근로지 종류 지정
        if (workplaceType == WorkplaceType.IN) {
            builder.and(workplace.workplaceType.eq(ConstString.WORKPLACE_TYPE_IN));
        } else if (workplaceType == WorkplaceType.OUT) {
            builder.and(workplace.workplaceType.eq(ConstString.WORKPLACE_TYPE_OUT));
        }

        // 정렬 옵션 지정
        if (sortOption == SortOption.WORKPLACE_NAME_ASC) { // 정렬 옵션이 근로지명 오름차순인 경우
            orderSpecifier = workplace.workplaceName.asc();
        } else if (sortOption == SortOption.WORKPLACE_NAME_DESC) {
            orderSpecifier = workplace.workplaceName.desc();
        } else if (sortOption == SortOption.WORKPLACE_VIEW_ASC) {
            orderSpecifier = workplace.views.asc();
        } else if (sortOption == SortOption.WORKPLACE_VIEW_DESC) {
            orderSpecifier = workplace.views.desc();
        } else if (sortOption == SortOption.WORKPLACE_COMMENT_NUM_ASC) {
            orderSpecifier = workplace.commentNum.asc();
        } else if (sortOption == SortOption.WORKPLACE_COMMENT_NUM_DESC) {
            orderSpecifier = workplace.commentNum.desc();
        }

        // 조건에 맞는 workplace를 가져옴
        List<Workplace> workplaceList = queryFactory
                .select(workplace)
                .from(workplace)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조건에 맞는 결과에 해당하는 총 workplace 갯수를 가져오기 위한 쿼리: SELECT count(*)
        JPAQuery<Long> countQuery = queryFactory
                .select(workplace.count())
                .from(workplace)
                .where(builder);

        // PageableExecutionUtils.getPage(): 페이지 사이즈보다 작을 경우 또는 마지막 페이지일 경우에는 추가적인 데이터베이스 쿼리를 실행하지 않음
        return PageableExecutionUtils.getPage(workplaceList, pageable,countQuery::fetchOne);

    }

    /**
     * 대학 별 근로지 요약정보를 가져오는 메소드, workplaceName과 sortOption에 따라 검색 및 정렬 가능한 메소드
     * @param pageable pageable 객체(서비스 계층에서 구현)
     * @param univCode 학교 코드
     * @param workplaceName @Nullable 근로지명(검색 옵션)
     * @param sortOption 정렬 조건 enum
     * @return Workplace 페이지 객체
     * @since 1.0.0
     * */
    @Override
    public Page<WorkplaceSummaryDto> getWorkplacesSummary(Pageable pageable, Long univCode, @Nullable String workplaceName, WorkplaceType workplaceType, SortOption sortOption) {

        QWorkplace workplace = QWorkplace.workplace;
        BooleanBuilder builder = new BooleanBuilder(); // 조건
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        // 근로지명 검색어가 존재할 경우
        if (StringUtils.hasText(workplaceName)) {
            builder.and(workplace.workplaceName.like("%" + workplaceName + "%"));
        }

        // 학교 코드 where 조건 설정
        builder.and(workplace.univCode.eq(univCode));

        // 근로지 종류 지정
        if (workplaceType == WorkplaceType.IN) {
            builder.and(workplace.workplaceType.eq(ConstString.WORKPLACE_TYPE_IN));
        } else if (workplaceType == WorkplaceType.OUT) {
            builder.and(workplace.workplaceType.eq(ConstString.WORKPLACE_TYPE_OUT));
        }

        // 정렬 옵션 지정
        if (sortOption == SortOption.WORKPLACE_NAME_ASC) { // 정렬 옵션이 근로지명 오름차순인 경우
            orderSpecifier = workplace.workplaceName.asc();
        } else if (sortOption == SortOption.WORKPLACE_NAME_DESC) {
            orderSpecifier = workplace.workplaceName.desc();
        } else if (sortOption == SortOption.WORKPLACE_VIEW_ASC) {
            orderSpecifier = workplace.views.asc();
        } else if (sortOption == SortOption.WORKPLACE_VIEW_DESC) {
            orderSpecifier = workplace.views.desc();
        } else if (sortOption == SortOption.WORKPLACE_COMMENT_NUM_ASC) {
            orderSpecifier = workplace.commentNum.asc();
        } else if (sortOption == SortOption.WORKPLACE_COMMENT_NUM_DESC) {
            orderSpecifier = workplace.commentNum.desc();
        }

        // 조건에 맞는 workplace를 가져옴
        List<WorkplaceSummaryDto> workplaceSummaryList = queryFactory
                .select(Projections.constructor(WorkplaceSummaryDto.class, workplace.univCode, workplace.workplaceCode, workplace.univName, workplace.workplaceName, workplace.workType, workplace.workplaceType, workplace.views, workplace.commentNum))
                .from(workplace)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 조건에 맞는 결과에 해당하는 총 workplace 갯수를 가져오기 위한 쿼리: SELECT count(*)
        JPAQuery<Long> countQuery = queryFactory
                .select(workplace.count())
                .from(workplace)
                .where(builder);
        return PageableExecutionUtils.getPage(workplaceSummaryList, pageable,countQuery::fetchOne);
    }
}
