package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.dto.requestDTO.prescription.CreatePrescriptionRequest;
import com.example.MediConnect_Backend.dto.responseDTO.prescription.PrescriptionResponse;
import com.example.MediConnect_Backend.entity.User;

import java.util.List;

public interface PrescriptionService {

    PrescriptionResponse createPrescription(Long appointmentId, User doctorUser, CreatePrescriptionRequest prescriptionDto);
    PrescriptionResponse getPrescriptionForAppointment(Long consultationId, User currentUser);
    List<PrescriptionResponse> getPrescriptionsForPatient(User patientUser);
}
