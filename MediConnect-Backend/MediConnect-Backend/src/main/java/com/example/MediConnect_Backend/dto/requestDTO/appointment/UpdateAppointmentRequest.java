package com.example.MediConnect_Backend.dto.requestDTO.appointment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAppointmentRequest {
    @NotBlank(message = "Reason cannot be blank")
    private String reason;
}
