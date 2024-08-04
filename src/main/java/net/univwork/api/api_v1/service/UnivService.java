package net.univwork.api.api_v1.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.WorkplaceSummaryDto;
import net.univwork.api.api_v1.domain.entity.University;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.enums.SortOption;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.repository.UnivRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UnivService {

    private final UnivRepository univRepository;

    public University getUniv(Long univCode) {
        return univRepository.getUniv(univCode);
    }

    /**
     * getUniversities: 학교들이 담긴 페이지를 반환, 정렬 및 검색 가능
     * @param pageNumber 페이지 숫자
     * @param pageLimit 한 페이지 당 갯수
     * @param univName @Nullable 학교 이름
     * @param sortOption 정렬 옵션
     * */
    public Page<University> getUniversities(int pageNumber, int pageLimit, @Nullable String univName, final SortOption sortOption) {

        // Controller에서 넘겨받은 pageNumber, pageLimit으로 pageable 생성
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);

        return univRepository.getUniversities(pageable, univName, sortOption);
    }


    /**
     * getWorkplaces: 학교별 근로지들을 반환, 정렬 및 검색 가능(근로지 페이지에서 추가 검색 고려)
     * @param univCode 학교 코드(정수_임시)
     * @param pageNumber 페이지 숫자
     * @param pageLimit 한 페이지 당 갯수
     * @param workplaceName @Nullable 근로지 검색어
     * @param sortOption 정렬 옵션
     * */
    public Page<Workplace> getWorkplaces(final Long univCode, final int pageNumber, final int pageLimit, @Nullable final String workplaceName, final WorkplaceType workplaceType, final SortOption sortOption) {

        // Controller에서 넘겨 받은 pageNumber, pageLimit 으로 pageable 생성
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);

        return univRepository.getWorkplaces(pageable, univCode, workplaceName, workplaceType, sortOption);
    }

    /**
     * getWorkplacesSummary: 학교별 근로지 요약정보들을 반환, 정렬 및 검색 가능(근로지 페이지에서 추가 검색 고려)
     * @param univCode 학교 코드(정수_임시)
     * @param pageNumber 페이지 숫자
     * @param pageLimit 한 페이지 당 갯수
     * @param workplaceName @Nullable 근로지 검색어
     * @param sortOption 정렬 옵션
     * */
    public Page<WorkplaceSummaryDto> getWorkplacesSummary(final Long univCode, final int pageNumber, final int pageLimit, @Nullable final String workplaceName, final WorkplaceType workplaceType, final SortOption sortOption){

        // Controller에서 넘겨 받은 pageNumber, pageLimit 으로 pageable 생성
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);

        return univRepository.getWorkplacesSummary(pageable, univCode, workplaceName, workplaceType, sortOption);
    }

    /**
     * countWorkplacePerUniv: 학교별 근로지 갯수를 반환
     * @param univCode 학교 코드(Long)
     * */
    public int countWorkplacePerUniv(Long univCode) {
        return univRepository.countWorkplacesPerUniv(univCode);
    }
}
