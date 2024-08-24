package net.univwork.api.api_v1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.Preview;
import net.univwork.api.api_v1.domain.entity.QWorkplaceComment;
import net.univwork.api.api_v1.domain.entity.Workplace;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceCommentRepository;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class WorkplaceRepositoryImpl implements WorkplaceRepository{

    private final JpaWorkplaceRepository jpaWorkplaceRepository;

    private final JpaWorkplaceCommentRepository jpaWorkplaceCommentRepository;

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public WorkplaceRepositoryImpl(JpaWorkplaceRepository jpaWorkplaceRepository,
                                   JpaWorkplaceCommentRepository jpaWorkplaceCommentRepository,
                                   EntityManager em) {
        this.jpaWorkplaceRepository = jpaWorkplaceRepository;
        this.jpaWorkplaceCommentRepository = jpaWorkplaceCommentRepository;
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

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
        QWorkplaceComment workplaceComment = QWorkplaceComment.workplaceComment;
        BooleanBuilder builder = new BooleanBuilder(); // 조건
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        builder.and(workplaceComment.univCode.eq(univCode));
        builder.and(workplaceComment.workplaceCode.eq(workplaceCode));
        builder.and(workplaceComment.deleteFlag.eq(false));

        orderSpecifier = workplaceComment.timestamp.desc();

        List<WorkplaceComment> workplaceComments = queryFactory
                .select(workplaceComment)
                .from(workplaceComment)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(workplaceComment.count())
                .from(workplaceComment)
                .where(builder);

        return PageableExecutionUtils.getPage(workplaceComments, pageable, countQuery::fetchOne);
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

    @Override
    public int countUserComments(final String userId, final Long univCode, final Long workplaceCode) {
        return jpaWorkplaceCommentRepository.countWorkplaceCommentsByUserIdAndUnivCodeAndWorkplaceCode(userId, univCode, workplaceCode);
    }

    @Override
    public int countCommentRating(Long univCode, Long workplaceCode) {
        QWorkplaceComment workplaceComment = QWorkplaceComment.workplaceComment;
        BooleanBuilder builder = new BooleanBuilder(); // 조건

        builder.and(workplaceComment.rating.isNotNull());
        builder.and(workplaceComment.deleteFlag.isFalse());
        builder.and(workplaceComment.univCode.eq(univCode));
        builder.and(workplaceComment.workplaceCode.eq(workplaceCode));
        Long cnt = queryFactory
                .select(workplaceComment.count())
                .from(workplaceComment)
                .where(builder)
                .fetchOne();
        return cnt != null ? cnt.intValue() : 0;
    }

    @Override
    public Double sumCommentRating(Long univCode, Long workplaceCode) {

        QWorkplaceComment workplaceComment = QWorkplaceComment.workplaceComment;
        BooleanBuilder builder = new BooleanBuilder(); // 조건

        builder.and(workplaceComment.rating.isNotNull());
        builder.and(workplaceComment.univCode.eq(univCode));
        builder.and(workplaceComment.workplaceCode.eq(workplaceCode));

        return queryFactory
                .select(workplaceComment.rating.sum())
                .from(workplaceComment)
                .where(builder)
                .fetchOne();
    }

    @Override
    public List<Preview> getPreview(Long previewCnt) {

        QWorkplaceComment workplaceComment = QWorkplaceComment.workplaceComment;
        BooleanBuilder builder = new BooleanBuilder(); // 조건
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        orderSpecifier = workplaceComment.timestamp.desc();
        builder.and(workplaceComment.deleteFlag.isFalse());
        return queryFactory
                .select(Projections.constructor(Preview.class, workplaceComment.univName, workplaceComment.workplaceName, workplaceComment.comment, workplaceComment.univCode, workplaceComment.workplaceCode, workplaceComment.rating, workplaceComment.upvote, workplaceComment.timestamp))
                .from(workplaceComment)
                .orderBy(orderSpecifier)
                .limit(previewCnt)
                .fetch();
    }
}
