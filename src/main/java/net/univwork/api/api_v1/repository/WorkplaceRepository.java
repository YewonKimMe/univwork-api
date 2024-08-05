package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkplaceRepository {

    /**
     * 근로지를 조회하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @return Workplace
     * @since 1.0.0
     * */
    Workplace getWorkplace(final Long univCode, final Long workplaceCode);

    /**
     * 특정 학교의 특정 근로지의 댓글을 가져오는 메소드
     * @param pageable 페이지 정보
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @return WorkplaceCommet 페이지
     * @since 1.0.0
     * */
    Page<WorkplaceComment> getWorkplaceComments(Pageable pageable, final Long univCode, final Long workplaceCode);

    /**
     * 근로지 조회수를 업데이트 (+1) 하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @since 1.0.0
     * */
    void updateView(final Long univCode, final Long workplaceCode);

    /**
     * 근로지 댓글을 저장하는 메소드
     * @param comment WorkplaceCommet 댓글
     * @since 1.0.0
     * */
    WorkplaceComment saveWorkplaceComment(WorkplaceComment comment);

    int countUserComments(final String userId, final Long univCode, final Long workplaceCode);

    int countCommentRating(Long univCode, Long workplaceCode);

    Double sumCommentRating(Long univCode, Long workplaceCode);


}
