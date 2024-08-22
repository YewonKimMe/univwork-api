package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.dto.Preview;
import net.univwork.api.api_v1.service.PreviewService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "preview", description = "홈화면 미리보기 기능")
@RestController
@RequestMapping(value = "/api/v1/preview", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreviewController {

    private final PreviewService previewService;
    @GetMapping
    public ResponseEntity<List<Preview>> getPreview(@RequestParam(name = "num") Long num) {

        List<Preview> previewList = previewService.getAFewPreview(num);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(15, TimeUnit.SECONDS))
                .body(previewList);
    }
}
