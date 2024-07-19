package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaAdminNoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findNoticeByNo(Long no);

    @Query("SELECT n from Notice n WHERE n.isDeleted = false order by n.noticeTimestamp desc")
    Page<Notice> getNoticePage(Pageable pageable);
}
