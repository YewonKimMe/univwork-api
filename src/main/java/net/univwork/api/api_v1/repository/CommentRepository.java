package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.entity.CommentLikeLog;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;

import java.util.Optional;

public interface CommentRepository {

    Optional<WorkplaceComment> findWorkplaceCommentByCommentUuid(byte[] uuid);

    void likeComment(Long commentCode);

    boolean checkDuplicatedLikeToIp(Long commentCode, String userIp);

    void saveCommentLog(CommentLikeLog commentLikeLog);
}
