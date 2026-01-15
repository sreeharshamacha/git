package com.notification.management.controller;

import com.notification.management.dto.ApiResponse;
import com.notification.management.dto.ChannelDTO;
import com.notification.management.service.ChannelService;
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

@RestController
@RequestMapping("/api/v1/channels")
@Slf4j
@Tag(name = "Channel Management", description = "Endpoints for managing notification channels")
public class ChannelController {

    private final ChannelService channelService;
    private final Optional<Tracer> tracer;

    public ChannelController(ChannelService channelService, Optional<Tracer> tracer) {
        this.channelService = channelService;
        this.tracer = tracer;
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new channel", description = "Creates a new notification channel")
    public ResponseEntity<ApiResponse<ChannelDTO>> addChannel(@Valid @RequestBody ChannelDTO request) {
        log.info("Request to add channel: {}", request.getChannelType());
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                channelService.addChannel(request)));
    }

    @GetMapping("/list")
    @Operation(summary = "List all channels", description = "Retrieves a list of all active channels")
    public ResponseEntity<ApiResponse<List<ChannelDTO>>> listChannels() {
        log.info("Request to list all channels");
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                channelService.listAllChannels()));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get channel by ID", description = "Retrieves details of a specific channel")
    public ResponseEntity<ApiResponse<ChannelDTO>> getChannel(@PathVariable Long id) {
        log.info("Request to get channel: {}", id);
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                channelService.getChannel(id)));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete channel", description = "Soft deletes a channel")
    public ResponseEntity<ApiResponse<String>> deleteChannel(@PathVariable Long id) {
        log.info("Request to delete channel: {}", id);
        channelService.deleteChannel(id);
        return ResponseEntity.ok(buildResponse(MessageConstants.SUCCESS_CODE, MessageConstants.SUCCESS_MSG,
                "Channel deleted successfully"));
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
