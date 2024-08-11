package net.univwork.api.api_v1.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UnivMapDto {

    private Long univCode;

    private String univName;

    private String domain;

    private Double lat;

    private Double lng;
}
