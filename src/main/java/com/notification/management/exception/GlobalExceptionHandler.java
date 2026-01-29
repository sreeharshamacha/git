package com.notification.management.exception;

import com.notification.management.dto.ApiResponse;
import com.notification.management.util.MessageConstants;
import io.micrometer.tracing.Tracer;
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
                                .body(ApiResponse.error(MessageConstants.GENERIC_ERROR_CODE,
                                                MessageConstants.ERROR_MSG_GENERIC, getTraceId()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
                log.warn("Validation failure: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(MessageConstants.VALIDATION_ERROR_CODE,
                                                e.getBindingResult().getFieldError() != null
                                                                ? e.getBindingResult().getFieldError()
                                                                                .getDefaultMessage()
                                                                : MessageConstants.ERROR_MSG_GENERIC,
                                                getTraceId()));
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
                log.warn("Business Exception: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(e.getErrorCode(), e.getMessage(), getTraceId()));
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
                log.warn("Illegal Argument: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(MessageConstants.INVALID_REQUEST_CODE, e.getMessage(),
                                                getTraceId()));
        }

        private String getTraceId() {
                return tracer.map(t -> t.currentSpan() != null ? t.currentSpan().context().traceId() : "N/A")
                                .orElse("N/A");
        }
}
