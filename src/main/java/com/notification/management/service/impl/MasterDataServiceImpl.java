package com.notification.management.service.impl;

import com.notification.management.dto.MasterDataRequest;
import com.notification.management.dto.MasterDataResponse;
import com.notification.management.entity.MasterData;
import com.notification.management.repository.MasterDataRepository;
import com.notification.management.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasterDataServiceImpl implements MasterDataService {

    private final MasterDataRepository masterDataRepository;

    @Override
    @Transactional
    public MasterDataResponse createMasterData(MasterDataRequest request) {
        log.info("Creating master data for category: {} with key: {}", request.getCategory(), request.getMasterKey());
        MasterData masterData = MasterData.builder().category(request.getCategory()).masterKey(request.getMasterKey())
                .masterValue(request.getMasterValue()).status(request.getStatus() != null ? request.getStatus() : "01")
                .isDelete("N").build();

        masterData = masterDataRepository.save(masterData);
        return mapToResponse(masterData);
    }

    @Override
    @Transactional
    public MasterDataResponse updateMasterData(MasterDataRequest request) {
        log.info("Updating master data with id: {}", request.getId());
        MasterData masterData = masterDataRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Master Data not found with id: " + request.getId()));

        masterData.setCategory(request.getCategory());
        masterData.setMasterKey(request.getMasterKey());
        masterData.setMasterValue(request.getMasterValue());
        if (request.getStatus() != null) {
            masterData.setStatus(request.getStatus());
        }

        masterData = masterDataRepository.save(masterData);
        return mapToResponse(masterData);
    }

    @Override
    public MasterDataResponse getMasterDataById(UUID id) {
        MasterData masterData = masterDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Master Data not found with id: " + id));
        return mapToResponse(masterData);
    }

    @Override
    public List<MasterDataResponse> getAllMasterData() {
        return masterDataRepository.findByIsDelete("N").stream().filter(m -> m.getIsActive() != null && m.getIsActive())
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<MasterDataResponse> getMasterDataByCategory(String category) {
        return masterDataRepository.findByCategoryAndIsActiveAndIsDelete(category, true, "N").stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<String> getMasterCategories() {
        return masterDataRepository.findDistinctCategories();
    }

    @Override
    @Transactional
    public void deleteMasterData(UUID id) {
        log.info("Deleting master data with id: {}", id);
        MasterData masterData = masterDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Master Data not found with id: " + id));
        masterData.setIsDelete("Y");
        masterDataRepository.save(masterData);
    }

    private MasterDataResponse mapToResponse(MasterData masterData) {
        return MasterDataResponse.builder().id(masterData.getId()).category(masterData.getCategory())
                .masterKey(masterData.getMasterKey()).masterValue(masterData.getMasterValue())
                .isActive(masterData.getIsActive()).status(masterData.getStatus()).createdBy(masterData.getCreatedBy())
                .updatedBy(masterData.getUpdatedBy()).createdDate(masterData.getCreatedDate())
                .updatedDate(masterData.getUpdatedDate()).build();
    }
}
