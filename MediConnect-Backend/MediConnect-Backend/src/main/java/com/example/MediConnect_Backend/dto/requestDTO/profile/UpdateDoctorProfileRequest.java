package com.example.MediConnect_Backend.dto.requestDTO.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDoctorProfileRequest {
    private String name;

    private String specialization;

    private int exp;

    private String qualification;
}
