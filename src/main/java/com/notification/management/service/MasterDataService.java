package com.notification.management.service;

import com.notification.management.dto.MasterDataRequest;
import com.notification.management.dto.MasterDataResponse;

import java.util.List;
import java.util.UUID;

public interface MasterDataService {
    MasterDataResponse createMasterData(MasterDataRequest request);

    MasterDataResponse updateMasterData(MasterDataRequest request);

    MasterDataResponse getMasterDataById(UUID id);

    List<MasterDataResponse> getAllMasterData();

    List<MasterDataResponse> getMasterDataByCategory(String category);

    List<String> getMasterCategories();

    void deleteMasterData(UUID id);
}
