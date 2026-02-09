package com.notification.management.repository;

import com.notification.management.entity.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MasterDataRepository extends JpaRepository<MasterData, UUID> {
    List<MasterData> findByCategoryAndIsActiveAndIsDelete(String category, Boolean isActive, String isDelete);

    @Query("SELECT DISTINCT m.category FROM MasterData m WHERE m.isActive = true AND m.isDelete = 'N'")
    List<String> findDistinctCategories();

    List<MasterData> findByIsDelete(String isDelete);
}
