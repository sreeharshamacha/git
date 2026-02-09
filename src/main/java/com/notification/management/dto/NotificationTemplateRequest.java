package com.notification.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateRequest {

    private UUID templateId;

    @NotBlank(message = "Template name is required")
    @Size(max = 100, message = "Template name must not exceed 100 characters")
    private String templateName;

    @NotBlank(message = "Template code is required")
    @Size(max = 50, message = "Template code must not exceed 50 characters")
    private String templateCode;

    @NotBlank(message = "Application code is required")
    @Size(max = 50, message = "Application code must not exceed 50 characters")
    private String applicationCode;

    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    @NotNull(message = "Channel ID is required")
    private Long channelId;

    @Size(max = 150, message = "Subject must not exceed 150 characters")
    private String subject;

    @Size(max = 50, message = "Master data category must not exceed 50 characters")
    private String masterDataCategory;

    private byte[] content;

    private String logo;

    private String banner;

    private String status;
}
