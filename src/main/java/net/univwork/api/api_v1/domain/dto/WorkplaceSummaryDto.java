package net.univwork.api.api_v1.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class WorkplaceSummaryDto {

    private Long univCode;

    private Long workplaceCode;

    private String univName;

    private String workplaceName;

    private String workType;

    private String workplaceType;

    private Long views;

    private Long commentNum;

    private Double rating;
}
