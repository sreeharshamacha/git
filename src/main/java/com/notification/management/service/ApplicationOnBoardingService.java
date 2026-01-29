package com.notification.management.service;

import com.notification.management.dto.ApplicationRequest;
import com.notification.management.dto.ApplicationResponse;

import java.util.List;
import java.util.UUID;

public interface ApplicationOnBoardingService {
    ApplicationResponse addApplication(ApplicationRequest request);

    List<ApplicationResponse> listAllApplications();

    ApplicationResponse getApplication(UUID id);

    ApplicationResponse editApplication(ApplicationRequest request);

    void deleteApplication(ApplicationRequest request);
}
