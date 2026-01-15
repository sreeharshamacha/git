package com.notification.management.exception;

import com.notification.management.dto.ApiResponse;
import com.notification.management.util.MessageConstants;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        private final Optional<Tracer> tracer;

        public GlobalExceptionHandler(Optional<Tracer> tracer) {
                this.tracer = tracer;
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
                log.error("Unhandled Exception caught: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(buildResponse(MessageConstants.GENERIC_ERROR_CODE,
                                                MessageConstants.ERROR_MSG_GENERIC, null));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
                log.warn("Validation failure: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(buildResponse(MessageConstants.VALIDATION_ERROR_CODE,
                                                MessageConstants.ERROR_MSG_GENERIC,
                                                e.getBindingResult().getFieldError().getDefaultMessage()));
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
                log.warn("Business Exception: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(buildResponse(e.getErrorCode(), e.getMessage(), null));
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
