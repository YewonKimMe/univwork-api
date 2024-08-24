package net.univwork.api.api_v1.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.CommentLikeLog;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.repository.jpa.JpaCommentLikeLogRepository;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceCommentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final JpaWorkplaceCommentRepository jpaRepository;

    private final JPAQueryFactory queryFactory;

    private final JpaCommentLikeLogRepository jpaCommentLikeLogRepository;

    public CommentRepositoryImpl(JpaWorkplaceCommentRepository jpaRepository, JpaCommentLikeLogRepository jpaCommentLikeLogRepository, EntityManager em) {
        this.jpaRepository = jpaRepository;
        this.jpaCommentLikeLogRepository = jpaCommentLikeLogRepository;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<WorkplaceComment> findWorkplaceCommentByCommentUuid(byte[] uuid) {
        return jpaRepository.findWorkplaceCommentByCommentUuid(uuid);
    }

    @Override
    public void likeComment(Long commentCode) {
//        QWorkplaceComment workplaceComment = QWorkplaceComment.workplaceComment;
//        BooleanBuilder builder = new BooleanBuilder(); // 조건
//
//        builder.and(workplaceComment.commentCode.eq(commentCode));
//
//        long affectedRows = queryFactory
//                .update(workplaceComment)
//                .set(workplaceComment.upvote, Expressions.numberTemplate(Integer.class, "{0} + 1", workplaceComment.upvote))
//                .where(builder)
//                .execute();
        jpaRepository.updateCommentLike(commentCode);
    }

    @Override
    public boolean checkDuplicatedLikeToIp(Long commentCode, String userIp) {
        Optional<WorkplaceComment> findWpCommentOpt = jpaRepository.findById(commentCode);

        if (findWpCommentOpt.isEmpty()) {
            log.error("[commentCode 로 댓글을 찾을 수 없음], commentCode={}", commentCode);
            throw new RuntimeException("해당 댓글이 존재하지 않음");
        }

        Optional<CommentLikeLog> findCommentLogOpt = jpaCommentLikeLogRepository.findCommentLikeLogByCommentCode(commentCode);

        if (findCommentLogOpt.isEmpty()) {
            return false;
        }
        CommentLikeLog commentLikeLog = findCommentLogOpt.get();

        return commentLikeLog.getIp().equals(userIp);
    }

    @Override
    public void saveCommentLog(CommentLikeLog commentLikeLog) {
        jpaCommentLikeLogRepository.save(commentLikeLog);
    }
}
