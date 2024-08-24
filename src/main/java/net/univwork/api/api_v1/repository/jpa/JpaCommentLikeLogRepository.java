package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.CommentLikeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCommentLikeLogRepository extends JpaRepository<CommentLikeLog, Long> {

    Optional<CommentLikeLog> findCommentLikeLogByCommentCode(Long commentCode);
}
