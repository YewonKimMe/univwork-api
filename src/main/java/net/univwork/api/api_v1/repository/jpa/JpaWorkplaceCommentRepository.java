package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaWorkplaceCommentRepository extends JpaRepository<WorkplaceComment, Long> {

    /**
     * 학교코드와 근로지 코드로 근로지 댓글들을 최근 순으로 정렬하여 가져오는 메소드
     * @param pageable 페이지에이블
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @return WorkplaceComment 가 담긴 페이지 객체
     * @since 1.0.0
     * */
    @Query("SELECT wc FROM WorkplaceComment wc WHERE wc.univCode = :univCode AND wc.workplaceCode = :workplaceCode AND wc.deleteFlag = false ORDER BY wc.timestamp DESC")
    Page<WorkplaceComment> getWorkplaceComments(Pageable pageable, @Param("univCode") Long univCode, @Param("workplaceCode") Long workplaceCode);

    int countWorkplaceCommentsByUserIdAndUnivCodeAndWorkplaceCode(String userId, Long univCode, Long workplaceCode);
}
