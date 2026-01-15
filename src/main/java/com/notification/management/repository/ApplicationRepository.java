package com.notification.management.repository;

import com.notification.management.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByIsDelete(String isDelete);

    Optional<Application> findByCode(String code);
}
