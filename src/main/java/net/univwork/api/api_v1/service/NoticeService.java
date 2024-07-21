package net.univwork.api.api_v1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.NoticeTitleDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.repository.NoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Page<NoticeTitleDto> getNotices(final int pageNumber, final int pageLimit) {

        Pageable pageable = PageRequest.of(pageNumber, pageLimit); // pageable 객체 생성

        return noticeRepository.getNotices(pageable);
    }

    public Notice getNoticeDetail(final Long noticeNo) {
        return noticeRepository.getNotice(noticeNo);
    }
}
