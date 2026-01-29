package com.notification.management.service;

import com.notification.management.dto.NotificationProcessRequest;

public interface NotificationService {
        void processNotification(NotificationProcessRequest request);
}
