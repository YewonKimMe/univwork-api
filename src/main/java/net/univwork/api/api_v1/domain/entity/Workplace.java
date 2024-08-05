package net.univwork.api.api_v1.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Workplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workplaceCode;

    @Column
    private Long univCode;

    @Column
    private String univName;

    @Column
    private String workType;

    @Column
    private String workplaceType;

    @Column
    private String workplaceName;

    @Column
    private String workplaceAddress;

    @Column
    private String workTime;

    @Column
    private String workDay;

    @Column
    private String requiredNum;

    @Column
    private String preferredDepartment;

    @Column
    private String preferredGrade;

    @Column
    private String jobDetail;

    @Column
    private String note;

    @Column
    private Long views;

    @Column
    private Long commentNum;

    @Column
    private Double rating;

    public Workplace() {
    }
}
