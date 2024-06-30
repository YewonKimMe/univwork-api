package net.univwork.api.api_v1.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.repository.jpa.JpaWorkplaceCommentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final JpaWorkplaceCommentRepository jpaRepository;

    @Override
    public Optional<WorkplaceComment> findWorkplaceCommentByCommentUuid(byte[] uuid) {
        return jpaRepository.findWorkplaceCommentByCommentUuid(uuid);
    }
}
