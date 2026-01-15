package com.notification.management.service;

import com.notification.management.dto.ApplicationRequest;
import com.notification.management.dto.ApplicationResponse;
import com.notification.management.entity.Application;
import com.notification.management.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationOnBoardingService {

    private final ApplicationRepository applicationRepository;

    @Transactional
    public ApplicationResponse addApplication(ApplicationRequest request) {
        Application application = Application.builder()
                .name(request.getApplicationName())
                .code(request.getApplicationCode())
                .department(request.getApplicationDepartment())
                .owner(request.getApplicationOwner())
                .email(request.getApplicationEmail())
                .status(request.getStatus())
                .isDelete("N")
                .build();

        Application saved = applicationRepository.save(application);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> listAllApplications() {
        return applicationRepository.findByIsDelete("N").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getApplication(UUID id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
        return mapToResponse(application);
    }

    @Transactional
    public ApplicationResponse editApplication(ApplicationRequest request) {
        if (request.getApplicationId() == null) {
            throw new RuntimeException("Application ID is required for update");
        }
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(
                        () -> new RuntimeException("Application not found with id: " + request.getApplicationId()));

        application.setName(request.getApplicationName());
        application.setCode(request.getApplicationCode());
        application.setDepartment(request.getApplicationDepartment());
        application.setOwner(request.getApplicationOwner());
        application.setEmail(request.getApplicationEmail());
        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }

        Application updated = applicationRepository.save(application);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteApplication(ApplicationRequest request) {
        if (request.getApplicationId() == null) {
            throw new RuntimeException("Application ID is required for deletion");
        }
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(
                        () -> new RuntimeException("Application not found with id: " + request.getApplicationId()));

        application.setIsDelete("Y");
        applicationRepository.save(application);
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .applicationId(application.getId())
                .applicationName(application.getName())
                .applicationCode(application.getCode())
                .applicationDepartment(application.getDepartment())
                .applicationOwner(application.getOwner())
                .applicationEmail(application.getEmail())
                .isDelete(application.getIsDelete())
                .status(application.getStatus())
                .createdBy(application.getCreatedBy())
                .updatedBy(application.getUpdatedBy())
                .createdDate(application.getCreatedDate())
                .updatedDate(application.getUpdatedDate())
                .build();
    }
}
