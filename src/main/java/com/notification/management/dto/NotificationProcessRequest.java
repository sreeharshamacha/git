package com.notification.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for processing notification requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationProcessRequest {

    @NotBlank(message = "Template code is required")
    private String templateCode;

    @NotBlank(message = "Application code is required")
    private String applicationCode;

    @NotEmpty(message = "Recipient email list cannot be empty")
    private List<@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format in emailTo") String> emailTo;

    private List<@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format in emailCc") String> emailCc;

    /**
     * Content in JSON format.
     */
    private Object content;
}
