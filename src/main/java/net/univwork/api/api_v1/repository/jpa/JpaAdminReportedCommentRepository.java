package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.ReportedComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaAdminReportedCommentRepository extends JpaRepository<ReportedComment, Long> {
    @Query("SELECT r from ReportedComment r where r.inProgress = true")
    Page<ReportedComment> getReportedCommentList(Pageable pageable);
}
