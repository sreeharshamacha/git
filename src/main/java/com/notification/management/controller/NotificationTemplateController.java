package com.notification.management.controller;

import com.notification.management.dto.ApiResponse;
import com.notification.management.dto.NotificationTemplateRequest;
import com.notification.management.dto.NotificationTemplateResponse;
import com.notification.management.service.NotificationTemplateService;
import com.notification.management.util.MessageConstants;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/templates")
@Slf4j
@Tag(name = "Notification Template Management", description = "Endpoints for managing notification templates")
public class NotificationTemplateController {

    private final NotificationTemplateService templateService;
    private final Optional<Tracer> tracer;
    private final SpringTemplateEngine templateEngine;

    public NotificationTemplateController(NotificationTemplateService templateService,
            Optional<Tracer> tracer,
            @org.springframework.beans.factory.annotation.Qualifier("stringTemplateEngine") SpringTemplateEngine templateEngine) {
        this.templateService = templateService;
        this.tracer = tracer;
        this.templateEngine = templateEngine;
    }

    @PostMapping("/preview")
    @Operation(summary = "Preview template content", description = "Renders thymeleaf content with mock data")
    public ResponseEntity<String> previewContent(@RequestBody com.notification.management.dto.PreviewRequest request) {
        log.info("Request to preview template content");
        try {
            Context ctx = new Context();
            if (request.getData() != null) {
                ctx.setVariables(request.getData());
            }
            String rendered = templateEngine.process(request.getContent(), ctx);
            return ResponseEntity.ok(rendered);
        } catch (Exception e) {
            log.error("Error rendering template preview: ", e);
            return ResponseEntity.internalServerError().body("Error rendering preview: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new template", description = "Creates a new notification template")
    public ResponseEntity<ApiResponse<NotificationTemplateResponse>> createTemplate(
            @Valid @RequestBody NotificationTemplateRequest request) {
        log.info("Request to create template: {}", request.getTemplateCode());
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                templateService.createTemplate(request)));
    }

    @GetMapping("/list")
    @Operation(summary = "List all templates", description = "Retrieves a list of all active notification templates")
    public ResponseEntity<ApiResponse<List<NotificationTemplateResponse>>> listTemplates() {
        log.info("Request to list all templates");
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                templateService.listAllTemplates()));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get template by ID", description = "Retrieves details of a specific notification template")
    public ResponseEntity<ApiResponse<NotificationTemplateResponse>> getTemplate(@PathVariable UUID id) {
        log.info("Request to get template: {}", id);
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                templateService.getTemplate(id)));
    }

    @PostMapping("/update")
    @Operation(summary = "Update template", description = "Updates details of an existing notification template")
    public ResponseEntity<ApiResponse<NotificationTemplateResponse>> updateTemplate(
            @Valid @RequestBody NotificationTemplateRequest request) {
        log.info("Request to update template: {}", request.getTemplateId());
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                templateService.updateTemplate(request)));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete template", description = "Soft deletes a notification template")
    public ResponseEntity<ApiResponse<String>> deleteTemplate(@PathVariable UUID id) {
        log.info("Request to delete template: {}", id);
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                "Template deleted successfully"));
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
