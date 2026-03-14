package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.dto.responseDTO.consultation.ConsultationResponse;
import com.example.MediConnect_Backend.dto.requestDTO.consultation.CreateConsultationRequest;

import java.util.List;

public interface ConsultationService {
    ConsultationResponse createConsultation(Long appointmentId, User doctorUser, CreateConsultationRequest consultationDto);
    ConsultationResponse getConsultationForAppointment(Long appointmentId, User currentUser);
    List<ConsultationResponse> getAllConsultationsForPatient(User patientUser);
}