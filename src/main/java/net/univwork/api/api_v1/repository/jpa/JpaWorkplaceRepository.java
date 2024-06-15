package net.univwork.api.api_v1.repository.jpa;

import net.univwork.api.api_v1.domain.entity.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaWorkplaceRepository extends JpaRepository<Workplace, Long> {

    /**
     * 학교 코드, 근로지 코드로 개별 근로지(상세정보)를 가져오는 JPA 메소드
     * @param univCode 학교코드
     * @param workplaceCode 근로지 코드
     * @return Workplace
     * @since 1.0.0
     * */
    Workplace getWorkplaceByUnivCodeAndWorkplaceCode(Long univCode, Long workplaceCode);

    /**
     * 근로지 조회수 업데이트 메소드
     * @param univCode 학교 코드
     * @param workplaceCode 근로지 코드
     * @since 1.0.0
     * */
    @Modifying
    @Query("UPDATE Workplace w SET w.views = w.views + 1 where w.univCode = :univCode AND w.workplaceCode = :workplaceCode")
    void updateWorkplaceView(@Param("univCode") final Long univCode, @Param("workplaceCode") final Long workplaceCode);

}
