package com.example.MediConnect_Backend.dto.requestDTO.prescription;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePrescriptionRequest {
    @NotBlank(message = "Medicines are required")
    private String medicines;

    private String dosage;

    private String frequency;

    @NotBlank(message = "Notes are required")
    private String notes;

    private LocalDate date;
}
