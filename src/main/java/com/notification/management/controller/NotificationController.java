package com.notification.management.controller;

import com.notification.management.dto.ApiResponse;
import com.notification.management.dto.NotificationProcessRequest;
import com.notification.management.exception.BusinessException;
import com.notification.management.service.NotificationService;
import com.notification.management.util.MessageConstants;
import io.micrometer.tracing.Tracer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Controller to handle notification related requests.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Slf4j
@RequiredArgsConstructor
public class NotificationController {

        private final NotificationService notificationService;
        private final Optional<Tracer> tracer;

        /**
         * Receives Post request to process Notification.
         * Performs manual validation of required fields.
         *
         * @param request the notification process request
         * @return acknowledgment response
         */
        @PostMapping("/process")
        public ResponseEntity<ApiResponse<String>> processNotification(
                        @Valid @RequestBody NotificationProcessRequest request) {
                log.info("Received notification process request for template code: {}",
                                request != null ? request.getTemplateCode() : "null");

                // 3) Validate request is not null and required fields exist
                if (request == null) {
                        log.warn("Notification request body is null");
                        throw new BusinessException(MessageConstants.INVALID_REQUEST_CODE,
                                        MessageConstants.ERROR_MSG_INVALID_REQUEST);
                }

                if (request.getTemplateCode() == null || request.getTemplateCode().isBlank()) {
                        log.warn("Validation failed: Template Code is missing or null");
                        throw new BusinessException(MessageConstants.TEMPLATE_CODE_MISSING_CODE,
                                        MessageConstants.ERROR_MSG_TEMPLATE_CODE_MISSING);
                }

                if (request.getApplicationCode() == null || request.getApplicationCode().isBlank()) {
                        log.warn("Validation failed: Application Code is missing or null");
                        throw new BusinessException(MessageConstants.DATA_MISSING_CODE,
                                        MessageConstants.ERROR_MSG_APP_CODE_MISSING);
                }

                // 6) Process the message using service
                notificationService.processNotification(request);

                // 7) Send the acknowledge response to sender
                log.info("Notification request acknowledged for template code: {}", request.getTemplateCode());
                return ResponseEntity.ok(ApiResponse.success("SUCCESS", getTraceId()));
        }

        private String getTraceId() {
                return tracer.map(t -> t.currentSpan() != null ? t.currentSpan().context().traceId() : "N/A")
                                .orElse("N/A");
        }
}
