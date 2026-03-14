package com.example.MediConnect_Backend.dto.requestDTO.appointment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookAppointmentRequest {
    @NotBlank(message = "Reason for visit is required")
    private String reason;

    @NotNull(message = "Availability ID is required.")
    private Long availabilityId;
}
