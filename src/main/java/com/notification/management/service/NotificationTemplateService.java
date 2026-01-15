package com.notification.management.service;

import com.notification.management.dto.NotificationTemplateRequest;
import com.notification.management.dto.NotificationTemplateResponse;
import com.notification.management.entity.Channel;
import com.notification.management.entity.NotificationTemplate;
import com.notification.management.repository.ChannelRepository;
import com.notification.management.repository.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationTemplateService {

    private final NotificationTemplateRepository templateRepository;
    private final ChannelRepository channelRepository;

    @Transactional
    public NotificationTemplateResponse createTemplate(NotificationTemplateRequest request) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new RuntimeException("Channel not found with id: " + request.getChannelId()));

        NotificationTemplate template = NotificationTemplate.builder()
                .name(request.getTemplateName())
                .applicationCode(request.getApplicationCode())
                .applicationId(request.getApplicationId())
                .channel(channel)
                .subject(request.getSubject())
                .content(request.getContent())
                .logo(request.getLogo())
                .banner(request.getBanner())
                .status(request.getStatus())
                .isDelete("N")
                .build();

        NotificationTemplate saved = templateRepository.save(template);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> listAllTemplates() {
        return templateRepository.findAll().stream()
                .filter(t -> !"Y".equals(t.getIsDelete()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationTemplateResponse getTemplate(UUID id) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
        return mapToResponse(template);
    }

    @Transactional
    public NotificationTemplateResponse updateTemplate(NotificationTemplateRequest request) {
        if (request.getTemplateId() == null) {
            throw new RuntimeException("Template ID is required for update");
        }

        NotificationTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + request.getTemplateId()));

        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new RuntimeException("Channel not found with id: " + request.getChannelId()));

        template.setName(request.getTemplateName());
        template.setApplicationCode(request.getApplicationCode());
        template.setApplicationId(request.getApplicationId());
        template.setChannel(channel);
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setLogo(request.getLogo());
        template.setBanner(request.getBanner());
        if (request.getStatus() != null) {
            template.setStatus(request.getStatus());
        }

        NotificationTemplate updated = templateRepository.save(template);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTemplate(UUID id) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
        template.setIsDelete("Y");
        templateRepository.save(template);
    }

    private NotificationTemplateResponse mapToResponse(NotificationTemplate template) {
        return NotificationTemplateResponse.builder()
                .templateId(template.getId())
                .templateName(template.getName())
                .applicationCode(template.getApplicationCode())
                .applicationId(template.getApplicationId())
                .channelId(template.getChannel() != null ? template.getChannel().getId() : null)
                .channelType(template.getChannel() != null ? template.getChannel().getType() : null)
                .subject(template.getSubject())
                .content(template.getContent())
                .logo(template.getLogo())
                .banner(template.getBanner())
                .status(template.getStatus())
                .isDelete(template.getIsDelete())
                .createdBy(template.getCreatedBy())
                .updatedBy(template.getUpdatedBy())
                .createdDate(template.getCreatedDate())
                .updatedDate(template.getUpdatedDate())
                .build();
    }
}
