package net.univwork.api.api_v1.domain.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
@Setter
public class UnivEmailDomainDto {
    private final String domain;

    private final String univName;
}
