package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.UnivWpEntryMapInfoDto;
import net.univwork.api.api_v1.enums.WorkplaceType;
import net.univwork.api.api_v1.service.MapService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Map", description = "좌표 관련 기능")
@RestController
@RequestMapping(value = "/api/v1/map", produces = MediaType.APPLICATION_JSON_VALUE)
public class MapInfoController {

    private final MapService mapService;

    // 1. 학교 코드로 호출 시 학교 좌표 정보 + 근로지 리스트 좌표 정보
    @GetMapping(value = "/university/{univCode}")
    public ResponseEntity<UnivWpEntryMapInfoDto> getUnivAndWorkplacePoint(@PathVariable(name = "univCode") Long univCode,
                                                      @RequestParam(name = "workplaceCode", required = false) Long workplaceCode,
                                                      @RequestParam(name = "workplaceType", required = false) String workplaceType) {
        WorkplaceType wpTypeEnum = WorkplaceType.fromValue(workplaceType);
        UnivWpEntryMapInfoDto entryUnivMapInfo = mapService.getEntryUnivMapInfo(univCode, workplaceCode, wpTypeEnum);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(entryUnivMapInfo);
    }
    // 2. 근로지 코드로 호출 시, 근로지 좌표 정보
}
