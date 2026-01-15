package com.notification.management.controller;

import com.notification.management.dto.ApiResponse;
import com.notification.management.dto.ApplicationRequest;
import com.notification.management.dto.ApplicationResponse;
import com.notification.management.service.ApplicationOnBoardingService;
import com.notification.management.util.MessageConstants;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/onboarding")
@Slf4j
@Tag(name = "Application Onboarding", description = "Endpoints for managing application onboarding and lifecycle")
public class ApplicationOnBoardingController {

    private final ApplicationOnBoardingService onboardingService;
    private final Optional<Tracer> tracer;

    public ApplicationOnBoardingController(ApplicationOnBoardingService onboardingService, Optional<Tracer> tracer) {
        this.onboardingService = onboardingService;
        this.tracer = tracer;
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new application", description = "Registers a new application in the system")
    public ResponseEntity<ApiResponse<ApplicationResponse>> addApplication(
            @Valid @RequestBody ApplicationRequest request) {
        log.info("Request to add application: {}", request.getApplicationName());
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                onboardingService.addApplication(request)));
    }

    @GetMapping("/list")
    @Operation(summary = "List all applications", description = "Retrieves a list of all active applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> listApplications() {
        log.info("Request to list all applications");
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                onboardingService.listAllApplications()));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get application by ID", description = "Retrieves details of a specific application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplication(@PathVariable UUID id) {
        log.info("Request to get application: {}", id);
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                onboardingService.getApplication(id)));
    }

    @PostMapping("/editOnboarding")
    @Operation(summary = "Edit application", description = "Updates details of an existing application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> editOnboarding(
            @Valid @RequestBody ApplicationRequest request) {
        log.info("Request to edit application: {}", request.getApplicationId());
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                onboardingService.editApplication(request)));
    }

    @PostMapping("/deleteOnboarding")
    @Operation(summary = "Delete application", description = "Soft deletes an application from the system")
    public ResponseEntity<ApiResponse<String>> deleteOnboarding(@RequestBody ApplicationRequest request) {
        log.info("Request to delete application: {}", request.getApplicationId());
        onboardingService.deleteApplication(request);
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                "Application deleted successfully"));
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
