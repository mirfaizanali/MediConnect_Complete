package com.example.MediConnect_Backend.dto.requestDTO.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.MediConnect_Backend.entity.Appointment;

@Data
public class UpdateAppointmentStatusRequest {
    @NotNull
    private Appointment.Status status;
}
