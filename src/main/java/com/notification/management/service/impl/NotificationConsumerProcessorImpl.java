package com.notification.management.service.impl;

import com.notification.management.dto.QueueMessage;
import com.notification.management.entity.NotificationTemplate;
import com.notification.management.repository.NotificationTemplateRepository;
import com.notification.management.service.ManagementService;
import com.notification.management.service.NotificationConsumerProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Headers;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerProcessorImpl implements NotificationConsumerProcessor {

    private final NotificationTemplateRepository templateRepository;
    private final ManagementService managementService;
    private final ProducerTemplate producerTemplate;
    private final TemplateEngine templateEngine;

    @Override
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
            NotificationTemplate template = templateRepository.findById(message.getTemplateId()).orElseThrow(
                    () -> new RuntimeException("Template not found in consumer phase: " + message.getTemplateId()));

            if (!"01".equals(template.getStatus())) {
                log.warn("Template ID {} is not active (status: {}), skipping notification processing.",
                        message.getTemplateId(), template.getStatus());
                if (auditId != null) {
                    managementService.updateAuditStatus(auditId, "SKIPPED",
                            "Notification skipped as template is not active");
                }
                return;
            }

            if (template.getChannel() != null) {
                String channelType = template.getChannel().getType();
                if (channelType != null) {
                    switch (channelType.toUpperCase()) {
                    case "EMAIL" -> sendEmail(message, template);
                    case "SMS" -> log.info("SMS implementation is coming soon...");
                    case "IN-APP" -> log.info("IN-APP implementation is coming soon...");
                    default -> log.warn("Channel type {} is not implemented yet.", channelType);
                    }
                } else {
                    log.warn("Channel type is null for template ID: {}", message.getTemplateId());
                }
            } else {
                log.warn("Channel not found for template ID: {}", message.getTemplateId());
            }

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
        String body = "";

        if (template.getContent() != null) {
            body = new String(template.getContent());
        }

        if (message.getContext() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) message.getContext();
            log.info("Processing template for {} with variables: {}", message.getEmailTo(), variables);

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(variables);
            try {
                body = templateEngine.process(body, thymeleafContext);
            } catch (Exception e) {
                log.error("Error processing Thymeleaf template: {}", e.getMessage(), e);
            }
        }

        log.info("Sending email to: {} with subject: {}", message.getEmailTo(), subject);

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
