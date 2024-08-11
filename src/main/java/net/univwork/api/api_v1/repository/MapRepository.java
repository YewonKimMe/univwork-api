package net.univwork.api.api_v1.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.UnivMapDto;
import net.univwork.api.api_v1.domain.dto.WorkplaceMapDto;
import net.univwork.api.api_v1.domain.entity.QUniversity;
import net.univwork.api.api_v1.domain.entity.QWorkplace;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.tool.ConstString;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class MapRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public MapRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public UnivMapDto getUnivMapDto(Long univCode) {
        QUniversity university = QUniversity.university;
        BooleanBuilder builder = new BooleanBuilder(); // 조건

        builder.and(university.univCode.eq(univCode));

        return queryFactory
                .select(Projections.constructor(UnivMapDto.class, university.univCode, university.univName, university.domain, university.lat, university.lng))
                .from(university)
                .where(builder)
                .fetchOne();
    }

    public List<WorkplaceMapDto> getWorkplaceMapDtoList(@NotNull Long univCode, @Nullable Long workplaceCode, final WorkplaceType workplaceType) {
        QWorkplace workplace = QWorkplace.workplace;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(workplace.univCode.eq(univCode));
        if (workplaceCode != null) {
            builder.and(workplace.workplaceCode.eq(workplaceCode));
        }
        if (workplaceType != null) {
            if (workplaceType == WorkplaceType.IN) {
                builder.and(workplace.workplaceType.eq(ConstString.WORKPLACE_TYPE_IN));
            } else if (workplaceType == WorkplaceType.OUT) {
                builder.and(workplace.workplaceType.eq(ConstString.WORKPLACE_TYPE_OUT));
            }
        }
        builder.and(workplace.lat.isNotNull());
        builder.and(workplace.lng.isNotNull());
        return queryFactory
                .select(Projections.constructor(WorkplaceMapDto.class, workplace.workplaceCode, workplace.univCode, workplace.univName, workplace.workplaceType, workplace.workplaceType, workplace.workplaceName, workplace.workplaceAddress, workplace.commentNum, workplace.rating, workplace.lat, workplace.lng))
                .from(workplace)
                .where(builder)
                .fetch();
    }
}
