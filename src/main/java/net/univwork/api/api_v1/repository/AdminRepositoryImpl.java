package net.univwork.api.api_v1.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.NoticeAdminDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.exception.NoticeNotFoundException;
import net.univwork.api.api_v1.repository.jpa.JpaAdminNoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {

    private final JpaAdminNoticeRepository jpaAdminNoticeRepository;

    @Override
    public Notice getNotice(Long no) {
        Optional<Notice> noticeOpt = jpaAdminNoticeRepository.findNoticeByNo(no);
        if (noticeOpt.isEmpty()) {
            throw new NoticeNotFoundException("해당 ID 로 발견된 공지사항이 없습니다. id = " + no);
        }
        return noticeOpt.get();
    }

    @Override
    public void saveNotice(Notice notice) {
        jpaAdminNoticeRepository.save(notice);
    }

    @Override
    public void updateNotice(NoticeAdminDto dto, Long no) {
        Optional<Notice> noticeOpt = jpaAdminNoticeRepository.findNoticeByNo(no);
        if (noticeOpt.isEmpty()) {
            throw new NoticeNotFoundException("해당 ID 로 발견된 공지사항이 없습니다. id = " + no);
        }
        Notice findNotice = noticeOpt.get();
        findNotice.setTitle(dto.getTitle());
        findNotice.setClassification(dto.getClassification());
        findNotice.setNoticeTimestamp(new Timestamp(System.currentTimeMillis()));
        findNotice.setContent(dto.getContent());
    }

    @Override
    public void deleteNotice(Long no) {
        Optional<Notice> noticeOpt = jpaAdminNoticeRepository.findNoticeByNo(no);
        if (noticeOpt.isEmpty()) {
            throw new NoticeNotFoundException("해당 ID 로 발견된 공지사항이 없습니다. id = " + no);
        }
        Notice findNotice = noticeOpt.get();
        findNotice.setDeleted(true);
    }

    @Override
    public Page<Notice> getNoticeList(Pageable pageable) {
        return jpaAdminNoticeRepository.getNoticePage(pageable);
    }
}
