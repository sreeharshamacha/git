package com.notification.management.service;

import com.notification.management.entity.ManagementAudit;
import com.notification.management.repository.ManagementAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ManagementService {

    private final ManagementAuditRepository auditRepository;

    public ManagementAudit logAudit(String action, String user, String status, String details) {
        log.info("Logging audit: {} by {} with status: {}", action, user, status);
        ManagementAudit audit = ManagementAudit.builder()
                .action(action)
                .performedBy(user)
                .timestamp(LocalDateTime.now())
                .status(status)
                .details(details)
                .build();
        return auditRepository.save(audit);
    }

    public void updateAuditStatus(Long id, String status, String details) {
        auditRepository.findById(id).ifPresent(audit -> {
            log.info("Updating audit ID: {} to status: {}", id, status);
            audit.setStatus(status);
            if (details != null) {
                audit.setDetails(details);
            }
            auditRepository.save(audit);
        });
    }

    public List<ManagementAudit> getAllAudits() {
        return auditRepository.findAll();
    }
}
