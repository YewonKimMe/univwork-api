package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.NoticeAdminDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.domain.entity.ReportedComment;
import net.univwork.api.api_v1.domain.entity.WorkplaceComment;
import net.univwork.api.api_v1.repository.AdminRepository;
import net.univwork.api.api_v1.repository.CommentRepository;
import net.univwork.api.api_v1.tool.UUIDConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    private final CommentRepository commentRepository;

    // 공지사항 리스트 획득 메소드
    public Page<Notice> getNoticeList(final int pageNumber, final int pageLimit) {
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);
        return adminRepository.getNoticeList(pageable);
    }

    // 공지사항 저장 메소드
    public void saveNotice(NoticeAdminDto noticeAdminDto, Authentication authentication) {
        Notice notice = Notice.builder()
                .noticeId(UUIDConverter.convertUuidStringToBinary16(UUID.randomUUID().toString()))
                .title(noticeAdminDto.getTitle())
                .classification(noticeAdminDto.getClassification())
                .author(noticeAdminDto.getAuthor())
                .hits(0)
                .noticeTimestamp(new Timestamp(System.currentTimeMillis()))
                .content(noticeAdminDto.getContent())
                .isFixed(false)
                .commentNum(0)
                .isDeleted(false)
                .build();
        adminRepository.saveNotice(notice);
        log.info("[Notice saved] Writer: {}", authentication.getName());
    }

    // 개별 공지사항 획득 메소드
    public Notice getNotice(Long no) {
        return adminRepository.getNotice(no);
    }
    public void editAndSaveNotice(NoticeAdminDto noticeAdminDto, Long no, Authentication authentication) {
        adminRepository.updateNotice(noticeAdminDto, no);
        log.info("[Notice edited] Editor: {}", authentication.getName());
    }

    // 공지사항 삭제 메소드
    public void deleteNotice(Long no, Authentication authentication) {
        // isDeleted flag = 1
        adminRepository.deleteNotice(no);
        log.info("[Notice deleted] Admin: {}", authentication.getName());
    }

    // 신고 댓글 획득 메소드
    public Page<ReportedComment> getReportedCommentList(final int pageNumber, final int pageLimit) {
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);
        return adminRepository.getReportedCommentList(pageable);
    }

    // 신고된 원본 댓글 삭제(soft) 메소드
    public void deleteCommentToReported(String commentUuid) {
        byte[] uuidBytes = UUIDConverter.uuidDecodeToByteArray(commentUuid);
        Optional<WorkplaceComment> commentOpt = commentRepository.findWorkplaceCommentByCommentUuid(uuidBytes);
        if (commentOpt.isEmpty()) {
            log.debug("uuid missing, uuid={}", UUIDConverter.convertBinary16ToUUID(uuidBytes).toString());
            throw new IllegalArgumentException("해당 UUID 로 검색된 댓글이 없습니다.");
        }
        WorkplaceComment workplaceComment = commentOpt.get();
        workplaceComment.setDeleteFlag(true);
    }
}
