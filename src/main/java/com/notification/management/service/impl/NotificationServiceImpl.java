package com.notification.management.service.impl;

import com.notification.management.dto.NotificationProcessRequest;
import com.notification.management.dto.QueueMessage;
import com.notification.management.entity.Application;
import com.notification.management.entity.ManagementAudit;
import com.notification.management.entity.NotificationTemplate;
import com.notification.management.exception.BusinessException;
import com.notification.management.repository.ApplicationRepository;
import com.notification.management.repository.NotificationTemplateRepository;
import com.notification.management.service.ManagementService;
import com.notification.management.service.NotificationService;
import com.notification.management.util.MessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

        private final ApplicationRepository applicationRepository;
        private final NotificationTemplateRepository templateRepository;
        private final ManagementService managementService;
        private final ProducerTemplate producerTemplate;

        @Override
        public void processNotification(NotificationProcessRequest request) {
                log.info("Starting notification processing for Template Code: {} and Application: {}",
                                request.getTemplateCode(), request.getApplicationCode());

                // 4) Check if Application Code exists and get Application ID
                Application application = applicationRepository.findByCode(request.getApplicationCode())
                                .orElseThrow(() -> {
                                        log.error("Application validation failed: {} does not exist",
                                                        request.getApplicationCode());
                                        return new BusinessException(MessageConstants.APP_NOT_FOUND_CODE,
                                                        MessageConstants.ERROR_MSG_APP_NOT_EXIST);
                                });

                if (!"01".equals(application.getStatus())) {
                        log.error("Application validation failed: {} is not active", request.getApplicationCode());
                        throw new BusinessException(MessageConstants.APP_NOT_ACTIVE_CODE, "Application is not active");
                }

                UUID applicationId = application.getId();
                log.debug("Found Application ID: {} for Code: {}", applicationId, request.getApplicationCode());

                // 5) Identify Template ID using Template Code and Application ID
                NotificationTemplate template = templateRepository
                                .findByApplicationIdAndTemplateCode(applicationId, request.getTemplateCode())
                                .orElseThrow(() -> {
                                        log.error("Template validation failed: {} does not exist for Application ID: {}",
                                                        request.getTemplateCode(), applicationId);
                                        return new BusinessException(MessageConstants.TEMPLATE_NOT_FOUND_CODE,
                                                        MessageConstants.ERROR_MSG_TEMPLATE_NOT_EXIST);
                                });

                if (!"01".equals(template.getStatus())) {
                        log.error("Template validation failed: {} is not active for Application ID: {}",
                                        request.getTemplateCode(), applicationId);
                        throw new BusinessException(MessageConstants.TEMPLATE_NOT_ACTIVE_CODE,
                                        "Template is not active");
                }

                UUID templateId = template.getId();
                log.debug("Found Template ID: {} for Code: {}", templateId, request.getTemplateCode());

                // 1) Construct the object json (QueueMessage)
                QueueMessage queueMessage = QueueMessage.builder()
                                .templateId(templateId)
                                .templateName(template.getName())
                                .templateCode(template.getTemplateCode())
                                .applicationCode(application.getCode())
                                .applicationId(applicationId)
                                .emailTo(request.getEmailTo())
                                .emailCc(request.getEmailCc())
                                .context(request.getContent())
                                .build();

                // g) Audit the details with status as in progress
                ManagementAudit audit = managementService.logAudit("PROCESS_NOTIFICATION", "SYSTEM", "IN_PROGRESS",
                                "Constructed message for template code: " + template.getTemplateCode());

                // 2) Create apache camel based rabbitMQ implementation and create a producer
                log.info("Sending message to RabbitMQ via Camel for processing...");
                Map<String, Object> headers = new HashMap<>();
                headers.put("auditId", audit.getId());

                producerTemplate.sendBodyAndHeaders("direct:sendToQueue", queueMessage, headers);

                log.info("Notification request submitted to queue for Template Code: {}", request.getTemplateCode());
        }
}
