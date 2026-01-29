package com.notification.management.service;

import com.notification.management.dto.QueueMessage;
import org.apache.camel.Headers;

import java.util.Map;

public interface NotificationConsumerProcessor {
    void process(QueueMessage message, @Headers Map<String, Object> headers);
}
