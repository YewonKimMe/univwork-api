package net.univwork.api.api_v1.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Preview {

    private String univName;

    private String workplaceName;

    private String commentPreview;

    private Long univCode;

    private Long workplaceCode;

    private Double rating;
}
