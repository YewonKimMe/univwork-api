package net.univwork.api.api_v1.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long univCode;

    @Column
    private String univName;

    @Column
    private String univUuid;

    @Column
    private String domain;

    @Column
    private String region;

    @Column
    private String academicSystem;

    @Column
    private Integer workplaceNum;

    public University() {
    }
}