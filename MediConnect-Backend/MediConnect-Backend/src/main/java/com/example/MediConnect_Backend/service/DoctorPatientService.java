package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.dto.responseDTO.history.PatientHistoryResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.PatientForDoctorResponse;
import com.example.MediConnect_Backend.entity.User;

import java.util.List;

public interface DoctorPatientService {
    List<PatientForDoctorResponse> getAssociatedPatients(User doctorUser);
    PatientHistoryResponse getPatientHistory(User doctorUser, Long patientId);
    List<PatientForDoctorResponse> searchPatients(User doctorUser, String name, String email);
}
