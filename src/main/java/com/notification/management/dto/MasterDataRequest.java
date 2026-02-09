package com.notification.management.dto;

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
public class MasterDataRequest {
    private UUID id;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @NotBlank(message = "Key is required")
    @Size(max = 50, message = "Key must not exceed 50 characters")
    private String masterKey;

    @NotBlank(message = "Value is required")
    @Size(max = 255, message = "Value must not exceed 255 characters")
    private String masterValue;

    private String status;
}
