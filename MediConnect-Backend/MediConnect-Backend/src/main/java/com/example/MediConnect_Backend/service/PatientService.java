package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.dto.responseDTO.profile.PatientProfileResponse;
import com.example.MediConnect_Backend.dto.requestDTO.profile.UpdatePatientProfileRequest;

import com.example.MediConnect_Backend.dto.responseDTO.history.PatientHistoryResponse;

public interface PatientService {
    PatientProfileResponse getPatientProfile(User currentUser);
    PatientProfileResponse updatePatientProfile(User currentUser, UpdatePatientProfileRequest profileDto);
    PatientHistoryResponse getPatientHistory(User currentUser);
}
