package com.notification.management.controller;

import com.notification.management.dto.ApiResponse;
import com.notification.management.dto.MasterDataRequest;
import com.notification.management.dto.MasterDataResponse;
import com.notification.management.service.MasterDataService;
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
@RequestMapping("/api/v1/master-data")
@Slf4j
@Tag(name = "Master Data Management", description = "Endpoints for managing key-value master data")
public class MasterDataController {

    private final MasterDataService masterDataService;
    private final Optional<Tracer> tracer;

    public MasterDataController(MasterDataService masterDataService, Optional<Tracer> tracer) {
        this.masterDataService = masterDataService;
        this.tracer = tracer;
    }

    @PostMapping("/add")
    @Operation(summary = "Add a new master data entry", description = "Creates a new key-value pair under a category")
    public ResponseEntity<ApiResponse<MasterDataResponse>> addMasterData(
            @Valid @RequestBody MasterDataRequest request) {
        log.info("Request to add master data: {} - {}", request.getCategory(), request.getMasterKey());
        return ResponseEntity.ok(ApiResponse.success(masterDataService.createMasterData(request), getTraceId()));
    }

    @PutMapping("/update")
    @Operation(summary = "Update an existing master data entry", description = "Updates details of an existing master data entry")
    public ResponseEntity<ApiResponse<MasterDataResponse>> updateMasterData(
            @Valid @RequestBody MasterDataRequest request) {
        log.info("Request to update master data: {}", request.getId());
        return ResponseEntity.ok(ApiResponse.success(masterDataService.updateMasterData(request), getTraceId()));
    }

    @GetMapping("/list")
    @Operation(summary = "List all master data", description = "Retrieves all active master data entries")
    public ResponseEntity<ApiResponse<List<MasterDataResponse>>> getAllMasterData() {
        log.info("Request to list all master data");
        return ResponseEntity.ok(ApiResponse.success(masterDataService.getAllMasterData(), getTraceId()));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get master data by category", description = "Retrieves all master data entries for a specific category")
    public ResponseEntity<ApiResponse<List<MasterDataResponse>>> getMasterDataByCategory(
            @PathVariable String category) {
        log.info("Request to get master data for category: {}", category);
        return ResponseEntity
                .ok(ApiResponse.success(masterDataService.getMasterDataByCategory(category), getTraceId()));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all master data categories", description = "Retrieves a list of all distinct master data categories")
    public ResponseEntity<ApiResponse<List<String>>> getMasterCategories() {
        log.info("Request to get all master data categories");
        return ResponseEntity.ok(ApiResponse.success(masterDataService.getMasterCategories(), getTraceId()));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Get master data by ID", description = "Retrieves a specific master data entry by its ID")
    public ResponseEntity<ApiResponse<MasterDataResponse>> getMasterDataById(@PathVariable UUID id) {
        log.info("Request to get master data: {}", id);
        return ResponseEntity.ok(ApiResponse.success(masterDataService.getMasterDataById(id), getTraceId()));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete master data", description = "Soft deletes a master data entry")
    public ResponseEntity<ApiResponse<String>> deleteMasterData(@PathVariable UUID id) {
        log.info("Request to delete master data: {}", id);
        masterDataService.deleteMasterData(id);
        return ResponseEntity.ok(ApiResponse.success("Master Data deleted successfully", getTraceId()));
    }

    private String getTraceId() {
        return tracer.map(t -> t.currentSpan() != null ? t.currentSpan().context().traceId() : "N/A").orElse("N/A");
    }
}
