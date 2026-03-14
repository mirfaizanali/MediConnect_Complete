package com.example.MediConnect_Backend.dto.responseDTO.consultation;

import lombok.Builder;
import lombok.Data;
import com.example.MediConnect_Backend.entity.Consultation;

import java.time.LocalDate;


@Data
@Builder
public class ConsultationResponse {
    private Long consultationId;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private LocalDate date;
    private String symptoms;
    private String bloodPressure;
    private int height;
    private int weight;
    private String description;
    private String notes;
    private Consultation.Status status;

    public static ConsultationResponse fromEntity(Consultation consultation) {
        return ConsultationResponse.builder()
                .consultationId(consultation.getConsultationId())
                .appointmentId(consultation.getAppointment().getAppointmentId())
                .patientId(consultation.getPatient().getId())
                .doctorId(consultation.getDoctor().getId())
                .date(consultation.getDate())
                .symptoms(consultation.getSymptoms())
                .bloodPressure(consultation.getBloodPressure())
                .height(consultation.getHeight())
                .weight(consultation.getWeight())
                .description(consultation.getDescription())
                .notes(consultation.getNotes())
                .status(consultation.getStatus())
                .build();
    }
}
