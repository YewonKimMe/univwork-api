package net.univwork.api.api_v1.repository;

import jakarta.annotation.Nullable;
import net.univwork.api.api_v1.domain.entity.University;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.enums.SortOption;
import net.univwork.api.api_v1.enums.WorkplaceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UnivRepository {

    /**
     * 대학교 리스트를 페이지로 가져오는 메소드
     * @param pageable 페이지 요청 객체
     * @param univName 학교명, null 가능
     * @param sortOption 정렬 옵션
     * @since 1.0.0
     * @apiNote 대학교 리스트 획득, 검색어 univName과 sortOption으로 검색 및 정렬 가능
     * */
    Page<University> getUniversities(Pageable pageable, @Nullable final String univName, final SortOption sortOption);

    /**
     * 근로지 리스트를 페이지로 가져오는 메소드
     * @param pageable 페이지 객체
     * @param univCode 학교 코드
     * @param workplaceName 근로지 이름
     * @param workplaceType 근로지 타입(enum)
     * @param sortOption 정렬 옵션(enum)
     * @apiNote 대학교 별 근로지 리스트 획득, 검색어 workplaceName 과 근로지 타입 workplaceType, sortOption 으로 정렬 및 필터링 가능
     * @since 1.0.0
     * */
    Page<Workplace> getWorkplaces(Pageable pageable, final Long univCode, @Nullable final String workplaceName, final WorkplaceType workplaceType, final SortOption sortOption);
}
