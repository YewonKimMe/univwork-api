package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.entity.WorkplaceComment;

import java.util.Optional;

public interface CommentRepository {

    Optional<WorkplaceComment> findWorkplaceCommentByCommentUuid(byte[] uuid);
}
