package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.dto.NoticeAdminDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.domain.entity.ReportedComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminRepository {

    Notice getNotice(Long no);

    void saveNotice(Notice notice);

    void updateNotice(NoticeAdminDto notice, Long no);

    void deleteNotice(Long no);

    Page<Notice> getNoticeList(Pageable pageable);

    Page<ReportedComment> getReportedCommentList(Pageable pageable);

    void blockUser(String userId);
}
