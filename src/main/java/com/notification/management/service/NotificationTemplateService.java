package com.notification.management.service;

import com.notification.management.dto.NotificationTemplateRequest;
import com.notification.management.dto.NotificationTemplateResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationTemplateService {
    NotificationTemplateResponse createTemplate(NotificationTemplateRequest request);

    List<NotificationTemplateResponse> listAllTemplates();

    NotificationTemplateResponse getTemplate(UUID id);

    NotificationTemplateResponse updateTemplate(NotificationTemplateRequest request);

    void deleteTemplate(UUID id);
}
