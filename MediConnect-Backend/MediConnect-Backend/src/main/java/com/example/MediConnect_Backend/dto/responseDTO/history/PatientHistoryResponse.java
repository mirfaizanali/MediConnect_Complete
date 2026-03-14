package com.example.MediConnect_Backend.dto.responseDTO.history;

import com.example.MediConnect_Backend.dto.responseDTO.prescription.PrescriptionResponse;
import com.example.MediConnect_Backend.entity.Patient;
import lombok.Builder;
import lombok.Data;
import com.example.MediConnect_Backend.dto.responseDTO.appointment.AppointmentResponseDTO;
import com.example.MediConnect_Backend.dto.responseDTO.consultation.ConsultationResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.PatientProfileResponse;

import java.util.List;

@Data
@Builder
public class PatientHistoryResponse {
    private PatientProfileResponse patientProfile;
    private List<AppointmentResponseDTO> appointments;
    private List<ConsultationResponse> consultations;
    private List<PrescriptionResponse> prescriptions;

    public static PatientHistoryResponse from(Patient patient,
                                              List<AppointmentResponseDTO> appointments,
                                              List<ConsultationResponse> consultations,
                                              List<PrescriptionResponse> prescriptions) {
        return PatientHistoryResponse.builder()
                .patientProfile(PatientProfileResponse.fromEntity(patient))
                .appointments(appointments)
                .consultations(consultations)
                .prescriptions(prescriptions)
                .build();
    }
}
