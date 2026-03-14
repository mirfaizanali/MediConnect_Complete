package com.example.MediConnect_Backend.dto.requestDTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDoctorRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String specialization;

    private int exp;
    private String qualification;
    private float rating;
}
