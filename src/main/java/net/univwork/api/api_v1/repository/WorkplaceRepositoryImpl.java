package net.univwork.api.api_v1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceCommentRepository;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class WorkplaceRepositoryImpl implements WorkplaceRepository{

    private final JpaWorkplaceRepository jpaWorkplaceRepository;

    private final JpaWorkplaceCommentRepository jpaWorkplaceCommentRepository;

    @PersistenceContext
    private final EntityManager em;

    /**
     * getWorkplace: 근로지 정보를 가져오는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @return Workplace
     * @since 1.0.0
     * */
    @Override
    public Workplace getWorkplace(Long univCode, Long workplaceCode) {
        return jpaWorkplaceRepository.getWorkplaceByUnivCodeAndWorkplaceCode(univCode, workplaceCode);
    }

    /**
     * 근로지 별 댓글을 가져오는 메소드
     * @since 1.0.0
     * @param pageable Pageable
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @return WorkplaceCommet Page
     * */
    @Override
    public Page<WorkplaceComment> getWorkplaceComments(Pageable pageable, final Long univCode, final Long workplaceCode) {
        return jpaWorkplaceCommentRepository.getWorkplaceComments(pageable, univCode, workplaceCode);
    }

    /**
     * 근로지 조회수를 업데이트 하는 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지코드
     * @since 1.0.0
     * */
    @Override
    public void updateView(Long univCode, Long workplaceCode) {
        jpaWorkplaceRepository.updateWorkplaceView(univCode, workplaceCode);
    }

    /**
     * 근로지 댓글 저장 메소드 saveWorkplaceComment();
     * @param comment WorkplaceComment
     * @return WorkplaceComment
     * @since 1.0.0
     * */
    @Override
    public WorkplaceComment saveWorkplaceComment(WorkplaceComment comment) {
        em.persist(comment);
        return comment;
    }
}
