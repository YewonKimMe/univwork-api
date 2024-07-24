package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "AWS Health Check", description = "AWS 로드 밸런서 상태 체크")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/aws/health-check", produces = MediaType.APPLICATION_JSON_VALUE)
public class AwsLoadBalancerCheckController {

    @GetMapping
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok().body("OK");
    }
}
