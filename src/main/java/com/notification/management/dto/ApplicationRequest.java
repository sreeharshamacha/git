package com.notification.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequest {

    private UUID applicationId; // Used for update/delete

    @NotBlank(message = "Application name is required")
    @Size(max = 100, message = "Application name must not exceed 100 characters")
    private String applicationName;

    @NotBlank(message = "Application code is required")
    @Size(max = 50, message = "Application code must not exceed 50 characters")
    private String applicationCode;

    @Size(max = 50, message = "Department must not exceed 50 characters")
    private String applicationDepartment;

    private String applicationOwner;

    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String applicationEmail;

    private String status;
}
