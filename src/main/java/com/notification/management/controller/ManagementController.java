package com.notification.management.controller;

import com.notification.management.dto.ApiResponse;
import com.notification.management.entity.ManagementAudit;
import com.notification.management.service.ManagementService;
import io.micrometer.tracing.Tracer;
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
        return ResponseEntity.ok(ApiResponse.success(managementService.getAllAudits(), getTraceId()));
    }

    @PostMapping("/test-action")
    public ResponseEntity<ApiResponse<String>> performTestAction() {
        log.info("Performing test action");
        managementService.logAudit("TEST_ACTION", "system-admin", "COMPLETED",
                "Execution of test action triggered via API");
        return ResponseEntity.ok(ApiResponse.success("Test action completed", getTraceId()));
    }

    @GetMapping("/health-check")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        log.info("Health check triggered");
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("status", "UP", "service", "Management Service"), getTraceId()));
    }

    private String getTraceId() {
        return tracer.map(t -> t.currentSpan() != null ? t.currentSpan().context().traceId() : "N/A")
                .orElse("N/A");
    }
}
