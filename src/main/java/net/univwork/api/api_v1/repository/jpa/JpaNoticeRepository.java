package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaNoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findNoticeByNo(Long no);
}
