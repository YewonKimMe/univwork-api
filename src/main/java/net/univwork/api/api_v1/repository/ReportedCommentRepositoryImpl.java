package net.univwork.api.api_v1.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.entity.ReportedComment;
import net.univwork.api.api_v1.repository.jpa.JpaReportedCommentRepository;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ReportedCommentRepositoryImpl implements ReportedCommentRepository {

    private final JpaReportedCommentRepository jpaReportedCommentRepository;
    @Override
    public void save(ReportedComment reportedComment) {
        jpaReportedCommentRepository.save(reportedComment);
    }

    @Override
    public void makeProgressOver(byte[] commentUuid) {
        ReportedComment reportedComment = jpaReportedCommentRepository.findReportedCommentByCommentUuid(commentUuid);

        reportedComment.setInProgress(false);
    }
}
