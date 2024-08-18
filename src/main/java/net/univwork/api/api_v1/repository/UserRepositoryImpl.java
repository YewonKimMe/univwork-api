package net.univwork.api.api_v1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.CommentDto;
import net.univwork.api.api_v1.domain.entity.QWorkplaceComment;
import net.univwork.api.api_v1.domain.entity.User;
import net.univwork.api.api_v1.repository.jpa.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository, EntityManager em) {
        this.jpaUserRepository = jpaUserRepository;
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<User> findUserByEmail(final String email) {
        return jpaUserRepository.findUserByEmail(email);
    }

    @Override
    public Optional<User> findUserByUserId(final String userId) {
        return jpaUserRepository.findUserByUserId(userId);
    }

    @Override
    public int updatePassword(final String userId, final String hashedPw) {
        return jpaUserRepository.updatePwd(userId, hashedPw);
    }

    @Override
    public int withdraw(final String userId) {
        return jpaUserRepository.deleteUserByUserId(userId);
    }

    @Override
    public Page<CommentDto> getCommentsPerUser(Pageable pageable, String username) {
        QWorkplaceComment workplaceComment = QWorkplaceComment.workplaceComment;
        BooleanBuilder builder = new BooleanBuilder();
        OrderSpecifier<?> orderSpecifier = null; // 정렬

        builder.and(workplaceComment.userId.eq(username));
        orderSpecifier = workplaceComment.timestamp.desc();

        List<CommentDto> userCommentList = queryFactory.select(Projections.constructor(CommentDto.class, workplaceComment.commentCode, workplaceComment.univCode, workplaceComment.workplaceCode, workplaceComment.workplaceName, workplaceComment.univName, workplaceComment.commentUuid, workplaceComment.comment, workplaceComment.upvote, workplaceComment.timestamp, workplaceComment.rating))
                .from(workplaceComment)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(workplaceComment.count())
                .from(workplaceComment)
                .where(builder);

        return PageableExecutionUtils.getPage(userCommentList, pageable, countQuery::fetchOne);
    }
}
