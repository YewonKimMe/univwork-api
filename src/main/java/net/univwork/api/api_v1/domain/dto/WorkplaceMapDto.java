package net.univwork.api.api_v1.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class WorkplaceMapDto {

    private Long workplaceCode;

    private Long univCode;

    private String univName;

    private String workType;

    private String workplaceType;

    private String workplaceName;

    private String workplaceAddress;

    private Long commentNum;

    private Double rating;

    private Double lat;

    private Double lng;
}
