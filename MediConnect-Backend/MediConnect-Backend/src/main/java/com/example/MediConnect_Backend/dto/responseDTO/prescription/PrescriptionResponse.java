package com.example.MediConnect_Backend.dto.responseDTO.prescription;


import com.example.MediConnect_Backend.entity.Prescription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class PrescriptionResponse {
    private Long prescriptionId;
    private Long consultationID;
    private Long patientId;
    private Long doctorId;
    private LocalDate date;
    private String medicines;
    private String frequency;
    private String dosage;
    private String notes;

    public static PrescriptionResponse fromEntity(Prescription prescription) {
        return PrescriptionResponse.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .consultationID(prescription.getConsultation().getConsultationId())
                .patientId(prescription.getPatient().getId())
                .doctorId(prescription.getDoctor().getId())
                .date(prescription.getDate())
                .medicines(prescription.getMedicines())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .notes(prescription.getNotes())
                .build();
    }
}
