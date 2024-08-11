package net.univwork.api.api_v1.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UnivWpEntryMapInfoDto {

    private UnivMapDto univMapDto;

    private List<WorkplaceMapDto> workplaceMapDtoList;
}
