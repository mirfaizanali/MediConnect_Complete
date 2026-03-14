package com.example.MediConnect_Backend.dto.requestDTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.example.MediConnect_Backend.enums.Gender;

@Data
public class RegisterPatientRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank
    private String name;

    @NotNull
    private Integer age;

    @NotBlank
    private String bloodGroup;

    @NotNull
    private long phoneNumber;

    @NotBlank
    private String address;

    @NotNull
    private Gender gender;
}
