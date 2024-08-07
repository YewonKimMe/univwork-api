package net.univwork.api.api_v1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.AdminAspectUserDetailDto;
import net.univwork.api.api_v1.domain.dto.NoticeAdminDto;
import net.univwork.api.api_v1.domain.entity.Notice;
import net.univwork.api.api_v1.domain.entity.QUser;
import net.univwork.api.api_v1.domain.entity.ReportedComment;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.exception.NoticeNotFoundException;
import net.univwork.api.api_v1.exception.UserNotExistException;
import net.univwork.api.api_v1.repository.jpa.JpaAdminNoticeRepository;
import net.univwork.api.api_v1.repository.jpa.JpaAdminReportedCommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private final JpaAdminNoticeRepository jpaAdminNoticeRepository;

    private final JpaAdminReportedCommentRepository jpaAdminReportedCommentRepository;

    private final UserRepository userRepository;

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public AdminRepositoryImpl(JpaAdminNoticeRepository jpaAdminNoticeRepository, JpaAdminReportedCommentRepository jpaAdminReportedCommentRepository, UserRepository userRepository, EntityManager em) {
        this.jpaAdminNoticeRepository = jpaAdminNoticeRepository;
        this.jpaAdminReportedCommentRepository = jpaAdminReportedCommentRepository;
        this.userRepository = userRepository;
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

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
        log.debug("dto: {}", dto);
        Notice findNotice = noticeOpt.get();
        findNotice.setTitle(dto.getTitle());
        findNotice.setClassification(dto.getClassification());
        findNotice.setNoticeTimestamp(new Timestamp(System.currentTimeMillis()));
        findNotice.setContent(dto.getContent());
        log.debug("Notice: {}", findNotice);
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

    @Override
    public Page<ReportedComment> getReportedCommentList(Pageable pageable) {
        return jpaAdminReportedCommentRepository.getReportedCommentList(pageable);
    }

    public void blockUser(String userId) {
        Optional<User> userOpt = userRepository.findUserByUserId(userId);
        if (userOpt.isEmpty()) {
            log.debug("userId={}", userId);
            throw new UserNotExistException("해당 ID의 유저가 존재하지 않습니다.");
        }
        User user = userOpt.get();
        user.setBlockedFlag(true);
    }

    @Override
    public Page<AdminAspectUserDetailDto> getUserList(Pageable pageable, String username) {
        QUser user = QUser.user;
        BooleanBuilder builder = new BooleanBuilder(); // 조건
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        if (username != null) {
            builder.and(user.userId.eq(username));
        }
        orderSpecifier = user.No.asc();
        List<AdminAspectUserDetailDto> userList = queryFactory
                .select(Projections.constructor(AdminAspectUserDetailDto.class, user.userId, user.email, user.createDate, user.univDomain, user.verification, user.blockedFlag))
                .from(user)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(builder);
        return PageableExecutionUtils.getPage(userList, pageable,countQuery::fetchOne);
    }
}
