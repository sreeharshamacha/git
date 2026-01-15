package com.notification.management.service;

import com.notification.management.dto.QueueMessage;
import com.notification.management.entity.NotificationTemplate;
import com.notification.management.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Headers;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor to handle consumed notification messages.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerProcessor {

    private final NotificationTemplateRepository templateRepository;
    private final ManagementService managementService;
    private final ProducerTemplate producerTemplate;

    /**
     * Processes the message from RabbitMQ.
     * 
     * @param message the unmarshaled QueueMessage
     * @param headers Camel message headers (containing audit ID)
     */
    @Transactional
    public void process(QueueMessage message, @Headers Map<String, Object> headers) {
        Object auditIdObj = headers.get("auditId");
        Long auditId = null;
        if (auditIdObj instanceof Number) {
            auditId = ((Number) auditIdObj).longValue();
        } else if (auditIdObj instanceof String) {
            try {
                auditId = Long.valueOf((String) auditIdObj);
            } catch (NumberFormatException e) {
                log.warn("Invalid auditId format: {}", auditIdObj);
            }
        }

        log.info("Processing notification for Template ID: {} with Audit ID: {}", message.getTemplateId(), auditId);

        try {
            // a) Identify template details
            NotificationTemplate template = templateRepository.findById(message.getTemplateId())
                    .orElseThrow(() -> new RuntimeException(
                            "Template not found in consumer phase: " + message.getTemplateId()));

            // b) Check channel type (Current requirement: EMAIL only)
            // c) EMAIL channel implementation
            if (template.getChannel() != null && "EMAIL".equalsIgnoreCase(template.getChannel().getType())) {
                sendEmail(message, template);
            } else {
                log.warn("Non-EMAIL channel types are not implemented yet.");
            }

            // Start the audit entry was done in producer, now mark it as completed.
            if (auditId != null) {
                managementService.updateAuditStatus(auditId, "COMPLETED",
                        "Notification sent successfully via Camel SMTP");
            }

        } catch (Exception e) {
            log.error("Error processing notification: ", e);
            if (auditId != null) {
                managementService.updateAuditStatus(auditId, "FAILED", "Error: " + e.getMessage());
            }
        }
    }

    private void sendEmail(QueueMessage message, NotificationTemplate template) {
        String subject = template.getSubject();
        String body = new String(template.getContent()); // Assuming content is body text

        // Replace placeholders in body using context map if available
        if (message.getContext() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> context = (Map<String, Object>) message.getContext();
            for (Map.Entry<String, Object> entry : context.entrySet()) {
                body = body.replace("{{" + entry.getKey() + "}}", String.valueOf(entry.getValue()));
            }
        }

        log.info("Sending email to: {} with subject: {}", message.getEmailTo(), subject);

        // d) Send email via Camel SMTP
        Map<String, Object> emailHeaders = new HashMap<>();
        emailHeaders.put("To", String.join(",", message.getEmailTo()));
        if (message.getEmailCc() != null && !message.getEmailCc().isEmpty()) {
            emailHeaders.put("Cc", String.join(",", message.getEmailCc()));
        }
        emailHeaders.put("Subject", subject);

        // producerTemplate.sendBodyAndHeaders("direct:smtpSend", body, emailHeaders);
        log.info("Email sent to: {}", message.getEmailTo());
    }
}
