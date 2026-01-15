package com.notification.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private UUID applicationId;
    private String applicationName;
    private String applicationCode;
    private String applicationDepartment;
    private String applicationOwner;
    private String applicationEmail;
    private String isDelete;
    private String status;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
