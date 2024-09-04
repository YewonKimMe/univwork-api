package net.univwork.api.api_v1.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class AddWorkplaceDto { // 근로지 추가 dto

    private Long univCode;

    private String workType;

    private String workplaceType;

    private String workplaceName;

    private String workplaceAddress;

    private String workTime;

    private String workDay;

    private String requiredNum;

    private String preferredDepartment;

    private String preferredGrade;

    private String jobDetail;

    private String note;
}
