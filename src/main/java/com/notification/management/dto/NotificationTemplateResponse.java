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
public class NotificationTemplateResponse {

    private UUID templateId;
    private String templateName;
    private String templateCode;
    private String applicationCode;
    private UUID applicationId;
    private Long channelId;
    private String channelType; // Useful to include the type name
    private String subject;
    private byte[] content;
    private String logo;
    private String banner;
    private String status;
    private String isDelete;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
