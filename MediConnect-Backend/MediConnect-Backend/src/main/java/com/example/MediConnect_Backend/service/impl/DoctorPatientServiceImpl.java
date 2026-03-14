package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.responseDTO.appointment.AppointmentResponseDTO;
import com.example.MediConnect_Backend.dto.responseDTO.consultation.ConsultationResponse;
import com.example.MediConnect_Backend.dto.responseDTO.history.PatientHistoryResponse;
import com.example.MediConnect_Backend.dto.responseDTO.prescription.PrescriptionResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.PatientForDoctorResponse;
import com.example.MediConnect_Backend.entity.*;
import com.example.MediConnect_Backend.repository.*;
import com.example.MediConnect_Backend.service.DoctorPatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoctorPatientServiceImpl implements DoctorPatientService {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Override
    public List<PatientForDoctorResponse> getAssociatedPatients(User doctorUser) {
        Doctor doctor = findDoctorByUser(doctorUser);
        List<Patient> patients = appointmentRepository.findDistinctPatientsByDoctor(doctor);
        List<PatientForDoctorResponse> responseList = new ArrayList<PatientForDoctorResponse>();

        for (Patient patient : patients) {
            PatientForDoctorResponse response = PatientForDoctorResponse.fromEntity(patient);
            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public PatientHistoryResponse getPatientHistory(User doctorUser, Long patientId) {
        Doctor doctor = findDoctorByUser(doctorUser);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        List<Appointment> appointments = appointmentRepository.findByDoctorAndPatientOrderByDateDesc(doctor, patient);
        if (appointments.isEmpty()) {
            throw new AccessDeniedException("You do not have permission to view this patient's history.");
        }

        List<Consultation> consultations = consultationRepository.findByPatientOrderByDateDesc(patient);
        List<Prescription> prescriptions = prescriptionRepository.findByPatientOrderByDateDesc(patient);

        List<AppointmentResponseDTO> appointmentDtos = appointments.stream().map(AppointmentResponseDTO::fromEntity).collect(Collectors.toList());
        List<ConsultationResponse> consultationDtos = consultations.stream().map(ConsultationResponse::fromEntity).collect(Collectors.toList());
        List<PrescriptionResponse> prescriptionDtos = prescriptions.stream().map(PrescriptionResponse::fromEntity).toList();

        return PatientHistoryResponse.from(patient, appointmentDtos, consultationDtos,prescriptionDtos);
    }

    @Override
    public List<PatientForDoctorResponse> searchPatients(User doctorUser, String name, String email) {
        Doctor doctor = findDoctorByUser(doctorUser);
        log.info("Searching patients for doctor: {}, name: {}, email: {}", doctor.getName(), name, email);

        List<Patient> patients;

        if (name != null && email != null) {
            patients = appointmentRepository.findDistinctPatientsByDoctorAndNameContainingAndEmailContaining(doctor, name, email);
            log.info("Found {} patients by name '{}' and email '{}'", patients.size(), name, email);
        } else if (name != null) {
            patients = appointmentRepository.findDistinctPatientsByDoctorAndNameContaining(doctor, name);
            log.info("Found {} patients by name '{}'", patients.size(), name);
        } else if (email != null) {
            patients = appointmentRepository.findDistinctPatientsByDoctorAndEmailContaining(doctor, email);
            log.info("Found {} patients by email '{}'", patients.size(), email);
        } else {
            patients = appointmentRepository.findDistinctPatientsByDoctor(doctor);
            log.info("No search criteria provided, returning all {} associated patients", patients.size());
        }


        List<PatientForDoctorResponse> responseList = new ArrayList<>();

        for (Patient patient : patients) {
            PatientForDoctorResponse response = PatientForDoctorResponse.fromEntity(patient);
            responseList.add(response);
        }

        return responseList;
    }

    private Doctor findDoctorByUser(User user) {
        return doctorRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for the current user."));
    }
}
