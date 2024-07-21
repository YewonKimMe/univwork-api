package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.ReportedComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReportedCommentRepository extends JpaRepository<ReportedComment, Long> {

    ReportedComment findReportedCommentByCommentUuid(byte[] commentUuid);
}
