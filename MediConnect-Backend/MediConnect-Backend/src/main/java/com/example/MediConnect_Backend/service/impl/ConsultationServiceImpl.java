package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.requestDTO.consultation.CreateConsultationRequest;
import com.example.MediConnect_Backend.dto.responseDTO.consultation.ConsultationResponse;
import com.example.MediConnect_Backend.entity.Appointment;
import com.example.MediConnect_Backend.entity.Consultation;
import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.AppointmentRepository;
import com.example.MediConnect_Backend.repository.ConsultationRepository;
import com.example.MediConnect_Backend.repository.PatientRepository;
import com.example.MediConnect_Backend.service.ConsultationService;
import com.example.MediConnect_Backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ConsultationResponse createConsultation(Long appointmentId, User doctorUser, CreateConsultationRequest consultationDto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getDoctor().getUser().getId().equals(doctorUser.getId())) {
            throw new AccessDeniedException("You do not have permission to create a consultation for this appointment.");
        }

        if (appointment.getStatus() != Appointment.Status.Booked) {
            throw new IllegalStateException("Consultation can only be created for 'Booked' appointments.");
        }

        System.out.print(consultationDto.getHeight());

        Consultation consultation = Consultation.builder()
                .appointment(appointment)
                .patient(appointment.getPatient())
                .doctor(appointment.getDoctor())
                .date(LocalDate.now())
                .symptoms(consultationDto.getSymptoms())
                .bloodPressure(consultationDto.getBloodPressure())
                .height(consultationDto.getHeight())
                .weight(consultationDto.getWeight())
                .description(consultationDto.getDescription())
                .notes(consultationDto.getNotes())
                .status(consultationDto.getStatus())
                .build();

        Consultation savedConsultation = consultationRepository.save(consultation);

        appointment.setStatus(Appointment.Status.Completed);
        appointmentRepository.save(appointment);

        String message = "Your consultation notes from Dr. " + appointment.getDoctor().getName() + " for your appointment on " + appointment.getDate() + " are now available.";
        notificationService.createNotification(appointment.getPatient().getUser(), message);

        return ConsultationResponse.fromEntity(savedConsultation);
    }

    @Override
    public ConsultationResponse getConsultationForAppointment(Long appointmentId, User currentUser) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

        Consultation consultation = consultationRepository.findByAppointment(appointment)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found for this appointment."));

        Long patientUserId = appointment.getPatient().getUser().getId();
        Long doctorUserId = appointment.getDoctor().getUser().getId();
        Long currentUserId = currentUser.getId();

        if (!currentUserId.equals(patientUserId) && !currentUserId.equals(doctorUserId)) {
            throw new AccessDeniedException("You do not have permission to view this consultation.");
        }

        return ConsultationResponse.fromEntity(consultation);
    }

    @Override
    public List<ConsultationResponse> getAllConsultationsForPatient(User patientUser) {
        Patient patient = patientRepository.findByUser(patientUser)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + patientUser.getEmail()));


        if (patient == null) {
            throw new IllegalArgumentException("Patient profile not found for the current user.");
        }

        List<Consultation> consultations = consultationRepository.findByPatientOrderByDateDesc(patient);

        List<ConsultationResponse> responses = new ArrayList<ConsultationResponse>();

        for (Consultation consultation : consultations) {
            ConsultationResponse dto = ConsultationResponse.fromEntity(consultation);
            responses.add(dto);
        }

        return responses;
    }
}
