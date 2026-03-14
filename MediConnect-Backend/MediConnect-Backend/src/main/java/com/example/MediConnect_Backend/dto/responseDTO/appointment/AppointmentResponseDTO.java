package com.example.MediConnect_Backend.dto.responseDTO.appointment;

import lombok.Builder;
import lombok.Data;
import com.example.MediConnect_Backend.entity.Appointment;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentResponseDTO {
    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private LocalDate date;
    private String timeSlot;
    private Appointment.Status status;
    private String reason;

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getAppointmentId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getName())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getName())
                .date(appointment.getDate())
                .timeSlot(appointment.getTimeSlot())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .build();
    }
}
