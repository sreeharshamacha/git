package com.notification.management.service;

import com.notification.management.entity.ManagementAudit;

import java.util.List;

public interface ManagementService {
    ManagementAudit logAudit(String action, String user, String status, String details);

    void updateAuditStatus(Long id, String status, String details);

    List<ManagementAudit> getAllAudits();
}
