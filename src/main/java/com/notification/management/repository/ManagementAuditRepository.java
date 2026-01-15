package com.notification.management.repository;

import com.notification.management.entity.ManagementAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagementAuditRepository extends JpaRepository<ManagementAudit, Long> {
}
