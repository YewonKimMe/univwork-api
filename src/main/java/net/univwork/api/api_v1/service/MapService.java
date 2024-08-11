package net.univwork.api.api_v1.service;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.UnivMapDto;
import net.univwork.api.api_v1.domain.dto.UnivWpEntryMapInfoDto;
import net.univwork.api.api_v1.domain.dto.WorkplaceMapDto;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.repository.MapRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MapService {

    private final MapRepository
            mapRepository;

    public UnivWpEntryMapInfoDto getEntryUnivMapInfo(Long univCode, @Nullable Long workplaceCode, WorkplaceType workplaceType) {
        UnivMapDto univMapDto = mapRepository.getUnivMapDto(univCode);
        List<WorkplaceMapDto> workplaceMapDtoList = mapRepository.getWorkplaceMapDtoList(univCode, workplaceCode, workplaceType);

        return new UnivWpEntryMapInfoDto(univMapDto, workplaceMapDtoList);
    }
}
