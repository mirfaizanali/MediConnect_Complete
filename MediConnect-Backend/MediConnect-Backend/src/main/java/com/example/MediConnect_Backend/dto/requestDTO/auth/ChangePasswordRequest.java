package com.example.MediConnect_Backend.dto.requestDTO.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;

    // Add confirmation field
    private String confirmPassword;

    // Optional: Add validation method to check if new passwords match
    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}