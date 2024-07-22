package net.univwork.api.api_v1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.NoticeTitleDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.domain.entity.QNotice;
import net.univwork.api.api_v1.repository.jpa.JpaNoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class NoticeRepositoryImpl implements NoticeRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    private final JpaNoticeRepository jpaNoticeRepository;

    public NoticeRepositoryImpl(JpaNoticeRepository jpaNoticeRepository, EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.jpaNoticeRepository = jpaNoticeRepository;
    }

    @Override
    public Page<NoticeTitleDto> getNotices(Pageable pageable) {
        QNotice notice = QNotice.notice;
        BooleanBuilder builder = new BooleanBuilder(); // 조건
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        builder.and(notice.isDeleted.eq(false));
        orderSpecifier = notice.noticeTimestamp.desc();

        List<NoticeTitleDto> noticesList = queryFactory
                .select(Projections.constructor(NoticeTitleDto.class, notice.no, notice.noticeId, notice.title, notice.classification, notice.author, notice.hits, notice.noticeTimestamp, notice.isFixed))
                .from(notice)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(notice.count())
                .from(notice)
                .where(builder);
        return PageableExecutionUtils.getPage(noticesList, pageable,countQuery::fetchOne);
    }

    @Override
    public Notice getNotice(final Long noticeNo) {
        Optional<Notice> noticeOpt = jpaNoticeRepository.findNoticeByNo(noticeNo);
        if (noticeOpt.isEmpty()) {
            log.debug("존재하지 않는 공지사항");
            throw new IllegalArgumentException("검색된 공지사항이 없습니다.");
        }
        Notice notice = noticeOpt.get();
        notice.setHits(notice.getHits() + 1);

        return notice;
    }
}
