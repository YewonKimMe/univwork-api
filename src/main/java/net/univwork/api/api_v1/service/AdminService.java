package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.NoticeAdminDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.repository.AdminRepository;
import net.univwork.api.api_v1.tool.UUIDConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public Page<Notice> getNoticeList(final int pageNumber, final int pageLimit) {
        Pageable pageable = PageRequest.of(pageNumber, pageLimit);
        return adminRepository.getNoticeList(pageable);
    }
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
    public Notice getNotice(Long no) {
        return adminRepository.getNotice(no);
    }
    public void editAndSaveNotice(NoticeAdminDto noticeAdminDto, Long no, Authentication authentication) {
        adminRepository.updateNotice(noticeAdminDto, no);
        log.info("[Notice edited] Editor: {}", authentication.getName());
    }

    public void deleteNotice(Long no, Authentication authentication) {
        // isDeleted flag = 1
        adminRepository.deleteNotice(no);
        log.info("[Notice deleted] Admin: {}", authentication.getName());
    }
}
