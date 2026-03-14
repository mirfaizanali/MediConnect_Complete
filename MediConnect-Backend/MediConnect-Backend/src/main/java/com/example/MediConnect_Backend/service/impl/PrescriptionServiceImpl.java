package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.requestDTO.prescription.CreatePrescriptionRequest;
import com.example.MediConnect_Backend.dto.responseDTO.prescription.PrescriptionResponse;
import com.example.MediConnect_Backend.entity.Appointment;
import com.example.MediConnect_Backend.entity.Consultation;
import com.example.MediConnect_Backend.entity.Prescription;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.AppointmentRepository;
import com.example.MediConnect_Backend.repository.ConsultationRepository;
import com.example.MediConnect_Backend.repository.PatientRepository;
import com.example.MediConnect_Backend.repository.PrescriptionRepository;
import com.example.MediConnect_Backend.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;


    @Override
    public PrescriptionResponse createPrescription(Long appointmentId, User doctorUser, CreatePrescriptionRequest prescriptionDto) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getDoctor().getUser().getId().equals(doctorUser.getId())) {
             throw new AccessDeniedException("You do not have permission to create a prescription for this appointment.");
        }

        Consultation consultation = consultationRepository.findByAppointment(appointment)
                .orElseGet(() -> {
                    Consultation newCons = Consultation.builder()
                            .appointment(appointment)
                            .patient(appointment.getPatient())
                            .doctor(appointment.getDoctor())
                            .date(appointment.getDate())
                            .status(Consultation.Status.Completed)
                            .build();
                    return consultationRepository.save(newCons);
                });

        Prescription prescription = Prescription.builder()
                .consultation(consultation)
                .patient(consultation.getPatient())
                .doctor(consultation.getDoctor())
                .date(appointment.getDate())
                .Medicines(prescriptionDto.getMedicines())
                .Dosage(prescriptionDto.getDosage())
                .Frequency(prescriptionDto.getFrequency())
                .notes(prescriptionDto.getNotes())
                .appointment(appointment)
                .build();

        Prescription savedPrescription = prescriptionRepository.save(prescription);

        consultation.setStatus(Consultation.Status.Completed);
        consultationRepository.save(consultation);

        return PrescriptionResponse.fromEntity(savedPrescription);
    }

    @Override
    public PrescriptionResponse getPrescriptionForAppointment(Long prescriptionId, User currentUser) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + prescriptionId));


        Long patientUserId = prescription.getPatient().getUser().getId();
        Long doctorUserId = prescription.getDoctor().getUser().getId();
        Long currentUserId = currentUser.getId();

        if (!currentUserId.equals(patientUserId) && !currentUserId.equals(doctorUserId)) {
            throw new AccessDeniedException("You do not have permission to view this prescription.");
        }

        return PrescriptionResponse.fromEntity(prescription);
    }

    @Override
    public java.util.List<PrescriptionResponse> getPrescriptionsForPatient(User patientUser) {
        com.example.MediConnect_Backend.entity.Patient patient = patientRepository.findByUser(patientUser)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found."));

        java.util.List<Prescription> prescriptions = prescriptionRepository.findByPatientOrderByDateDesc(patient);

        return prescriptions.stream()
                .map(PrescriptionResponse::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}
