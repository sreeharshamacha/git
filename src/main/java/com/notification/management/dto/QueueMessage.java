package com.notification.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO representing the message sent to and received from RabbitMQ.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueMessage {
    private UUID templateId;
    private String templateName;
    private String templateCode;
    private String applicationCode;
    private UUID applicationId;
    private List<String> emailTo;
    private List<String> emailCc;
    private Object context;
}
