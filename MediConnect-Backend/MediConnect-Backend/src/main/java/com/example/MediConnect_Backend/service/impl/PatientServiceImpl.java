package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.responseDTO.prescription.PrescriptionResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.PatientProfileResponse;
import com.example.MediConnect_Backend.entity.*;
import com.example.MediConnect_Backend.repository.*;
import com.example.MediConnect_Backend.service.PatientService;
import com.example.MediConnect_Backend.dto.requestDTO.profile.UpdatePatientProfileRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.MediConnect_Backend.dto.responseDTO.appointment.AppointmentResponseDTO;
import com.example.MediConnect_Backend.dto.responseDTO.consultation.ConsultationResponse;
import com.example.MediConnect_Backend.dto.responseDTO.history.PatientHistoryResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionRepository prescriptionRepository;

    public PatientServiceImpl(PatientRepository patientRepository, AppointmentRepository appointmentRepository, ConsultationRepository consultationRepository, PrescriptionRepository prescriptionRepository){
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.consultationRepository = consultationRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    private Patient findPatientByUser(User user) {
        return patientRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found for the current user."));
    }

    @Override
    public PatientProfileResponse getPatientProfile(User currentUser) {
        Patient patient = findPatientByUser(currentUser);
        return  PatientProfileResponse.fromEntity(patient);
    }

    @Override
    @Transactional
    public PatientProfileResponse updatePatientProfile (User currentUser , UpdatePatientProfileRequest profileDto){
        Patient patient = findPatientByUser(currentUser);
        patient.setName(profileDto.getName());
        patient.setAge(profileDto.getAge());
        patient.setBloodGroup(profileDto.getBloodGroup());
        patient.setPhoneNumber(profileDto.getPhoneNumber());
        patient.setAddress(profileDto.getAddress());
        patient.setGender(profileDto.getGender());

        Patient updatedPatient = patientRepository.saveAndFlush(patient);

        return PatientProfileResponse.fromEntity(updatedPatient);
    }

    @Override
    public PatientHistoryResponse getPatientHistory(User currentUser) {
        Patient patient = findPatientByUser(currentUser);

        List<Appointment> appointments = appointmentRepository.findByPatientOrderByDateDesc(patient);
        List<Consultation> consultations = consultationRepository.findByPatientOrderByDateDesc(patient);
        List<Prescription> prescriptions = prescriptionRepository.findByPatientOrderByDateDesc(patient);

        List<AppointmentResponseDTO> appointmentDtos = appointments.stream().map(AppointmentResponseDTO::fromEntity).collect(Collectors.toList());
        List<ConsultationResponse> consultationDtos = consultations.stream().map(ConsultationResponse::fromEntity).collect(Collectors.toList());
        List<PrescriptionResponse> prescriptionDtos = prescriptions.stream().map(PrescriptionResponse::fromEntity).toList();

        return PatientHistoryResponse.from(patient, appointmentDtos, consultationDtos,prescriptionDtos);
    }
}
