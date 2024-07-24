package net.univwork.api.api_v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.univwork.api.api_v1.domain.response.ResultAndMessage;
import net.univwork.api.api_v1.domain.response.SuccessResultAndMessage;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "aws", description = "aws health check")
    @GetMapping
    public ResponseEntity<ResultAndMessage> checkHealth() {
        return ResponseEntity.ok().body(new SuccessResultAndMessage(HttpStatus.OK.getReasonPhrase(), "OK"));
    }
}
