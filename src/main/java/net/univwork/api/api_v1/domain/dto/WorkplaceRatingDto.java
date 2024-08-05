package net.univwork.api.api_v1.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkplaceRatingDto {

    private Double average;

    private Integer ratingNum;
}
