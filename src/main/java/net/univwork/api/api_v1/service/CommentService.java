package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.exception.AlreadyReportedException;
import net.univwork.api.api_v1.repository.CommentRepository;
import net.univwork.api.api_v1.tool.UUIDConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void reportComment(String commentUuid) {
        byte[] commentUuidBytes = UUIDConverter.convertUuidStringToBinary16(commentUuid);
        Optional<WorkplaceComment> findCommentOpt = commentRepository.findWorkplaceCommentByCommentUuid(commentUuidBytes);
        if (findCommentOpt.isEmpty()) {
            log.error("해당 댓글이 존재하지 않습니다, Param UUID = {}", commentUuid);
            throw new IllegalArgumentException("잘못된 댓글 코드입니다.");
        }
        WorkplaceComment workplaceComment = findCommentOpt.get();
        if (workplaceComment.isReportFlag()) {
            throw new AlreadyReportedException("이미 신고된 댓글입니다.");
        }
        workplaceComment.setReportFlag(true);
        log.info("근로지 댓글이 신고되었습니다. UUID: {}, TIME: {}", commentUuid, new Timestamp(System.currentTimeMillis()));
    }
}
