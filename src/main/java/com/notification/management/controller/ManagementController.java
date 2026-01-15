package com.notification.management.controller;

import com.notification.management.dto.ApiResponse;
import com.notification.management.entity.ManagementAudit;
import com.notification.management.service.ManagementService;
import com.notification.management.util.MessageConstants;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/management")
@Slf4j
public class ManagementController {

    private final ManagementService managementService;
    private final Optional<Tracer> tracer;

    public ManagementController(ManagementService managementService, Optional<Tracer> tracer) {
        this.managementService = managementService;
        this.tracer = tracer;
    }

    @GetMapping("/audits")
    public ResponseEntity<ApiResponse<List<ManagementAudit>>> getAudits() {
        log.info("Fetching all management audits");
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                managementService.getAllAudits()));
    }

    @PostMapping("/test-action")
    public ResponseEntity<ApiResponse<String>> performTestAction() {
        log.info("Performing test action");
        managementService.logAudit("TEST_ACTION", "system-admin", "COMPLETED",
                "Execution of test action triggered via API");
        return ResponseEntity.ok(
                buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG, "Test action completed"));
    }

    @GetMapping("/health-check")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        log.info("Health check triggered");
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                Map.of("status", "UP", "service", "Management Service")));
    }

    private <T> ApiResponse<T> buildResponse(String code, String message, T data) {
        String traceId = tracer.map(t -> t.currentSpan() != null ? t.currentSpan().context().traceId() : "N/A")
                .orElse("N/A");

        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .traceId(traceId)
                .build();
    }
}
