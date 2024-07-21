package net.univwork.api.api_v1.repository;

import net.univwork.api.api_v1.domain.dto.NoticeTitleDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepository {

    Page<NoticeTitleDto> getNotices(Pageable pageable);

    Notice getNotice(final Long noticeNo);
}
