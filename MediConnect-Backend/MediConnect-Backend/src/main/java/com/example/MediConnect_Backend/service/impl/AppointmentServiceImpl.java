package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.requestDTO.appointment.BookAppointmentRequest;
import com.example.MediConnect_Backend.dto.responseDTO.appointment.AppointmentResponseDTO;
import com.example.MediConnect_Backend.entity.Appointment;
import com.example.MediConnect_Backend.entity.DoctorAvailability;
import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.entity.Doctor;
import com.example.MediConnect_Backend.enums.SlotStatus;
import com.example.MediConnect_Backend.repository.*;
import com.example.MediConnect_Backend.service.AppointmentService;
import com.example.MediConnect_Backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    @Override
    @Transactional
    public AppointmentResponseDTO bookAppointment(User patientUser, BookAppointmentRequest bookingDetails) {
        log.info("Attempting to book slot ID: {} for user: {}", bookingDetails.getAvailabilityId(), patientUser.getEmail());
        Patient patient = patientRepository.findByUser(patientUser)
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found."));

        DoctorAvailability slot = availabilityRepository
                .findByAvailabilityIdAndStatus(bookingDetails.getAvailabilityId(), SlotStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalStateException("This time slot is no longer available."));

        slot.setStatus(SlotStatus.PENDING);
        availabilityRepository.save(slot);
        log.info("Slot {} successfully marked as PENDING.", slot.getAvailabilityId());

        Appointment newAppointment = Appointment.builder()
                .patient(patient)
                .doctor(slot.getDoctor())
                .date(slot.getDate())
                .timeSlot(slot.getTimeSlot())
                .reason(bookingDetails.getReason())
                .specialty(slot.getDoctor().getSpecialization())
                .status(Appointment.Status.Waiting)
                .availability(slot) // <-- FIX: Create the link to the slot
                .build();

        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        String notificationMessage = "You have a new appointment request from " + patient.getName() + " for " + savedAppointment.getDate();
        notificationService.createNotification(slot.getDoctor().getUser(), notificationMessage);

        return AppointmentResponseDTO.fromEntity(savedAppointment);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsForDoctor(User doctorUser) {
        log.info("Getting appointments for doctor email: {}", doctorUser.getEmail());

        Optional<Doctor> doctorOptional = doctorRepository.findByUser(doctorUser);

        if (doctorOptional.isEmpty()) {
            throw new IllegalArgumentException("Doctor profile not found.");
        }

        Doctor doctor = doctorOptional.get();

        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);
        log.info("Found {} appointments for doctor: {}", appointments.size(), doctor.getName());

        List<AppointmentResponseDTO> responseList = new ArrayList<AppointmentResponseDTO>();
        for (Appointment appointment : appointments) {
            responseList.add(AppointmentResponseDTO.fromEntity(appointment));
        }

        return responseList;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(Long appointmentId, User doctorUser, Appointment.Status newStatus) {
        log.info("Updating appointment ID {} to status: {}", appointmentId, newStatus);

        Doctor doctor = doctorRepository.findByUser(doctorUser)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found."));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this appointment.");
        }

        DoctorAvailability slot = appointment.getAvailability();
        if (slot != null) {
            if (newStatus == Appointment.Status.Booked) {
                slot.setStatus(SlotStatus.BOOKED);
                log.info("Slot {} marked as BOOKED following appointment confirmation.", slot.getAvailabilityId());
            } else if (newStatus == Appointment.Status.Cancelled) {
                slot.setStatus(SlotStatus.AVAILABLE);
                log.info("Slot {} marked as AVAILABLE following appointment cancellation.", slot.getAvailabilityId());
            }
            availabilityRepository.save(slot);
        }

        appointment.setStatus(newStatus);
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        String statusText = newStatus == Appointment.Status.Booked ? "confirmed" : "cancelled";
        String notificationMessage = doctor.getName() + " has " + statusText + " your appointment for " + updatedAppointment.getDate();
        notificationService.createNotification(appointment.getPatient().getUser(), notificationMessage);

        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsForPatient(User patientUser) {
        log.info("Getting all appointments for patient email: {}", patientUser.getEmail());

        Optional<Patient> patientOptional = patientRepository.findByUser(patientUser);

        if (patientOptional.isEmpty()) {
            throw new IllegalArgumentException("Doctor profile not found.");
        }

        Patient patient = patientOptional.get();

        List<Appointment> appointments = appointmentRepository.findByPatient(patient);
        log.info("Found {} appointments for patient: {}", appointments.size(), patient.getName());

        List<AppointmentResponseDTO> responseList = new ArrayList<AppointmentResponseDTO>();

        for (Appointment appointment : appointments) {
            AppointmentResponseDTO dto = AppointmentResponseDTO.fromEntity(appointment);
            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public List<AppointmentResponseDTO> getUpcomingAppointmentsForPatient(User patientUser) {
        log.info("Getting upcoming appointments for patient email: {}", patientUser.getEmail());

        Optional<Patient> patientOptional = patientRepository.findByUser(patientUser);

        if (patientOptional.isEmpty()) {
            throw new IllegalArgumentException("Doctor profile not found.");
        }

        Patient patient = patientOptional.get();
        LocalDate today = LocalDate.now();

        List<Appointment> appointments = appointmentRepository.findByPatientAndDateGreaterThanEqualOrderByDateAscTimeSlotAsc(patient, today);
        log.info("Found {} upcoming appointments for patient: {}", appointments.size(), patient.getName());


        List<AppointmentResponseDTO> responseList = new ArrayList<AppointmentResponseDTO>();

        for (Appointment appointment : appointments) {
            AppointmentResponseDTO dto = AppointmentResponseDTO.fromEntity(appointment);

            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentHistoryForPatient(User patientUser) {
        log.info("Getting appointment history for patient email: {}", patientUser.getEmail());

        Optional<Patient> patientOptional = patientRepository.findByUser(patientUser);

        if (patientOptional.isEmpty()) {
            throw new IllegalArgumentException("Doctor profile not found.");
        }

        Patient patient = patientOptional.get();
        LocalDate today = LocalDate.now();

        List<Appointment> appointments = appointmentRepository.findByPatientAndDateBeforeOrderByDateDescTimeSlotDesc(patient, today);
        log.info("Found {} historical appointments for patient: {}", appointments.size(), patient.getName());


        List<AppointmentResponseDTO> responseList = new ArrayList<AppointmentResponseDTO>();

        for (Appointment appointment : appointments) {
            AppointmentResponseDTO dto = AppointmentResponseDTO.fromEntity(appointment);

            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateAppointmentReason(Long appointmentId, User patientUser, String newReason) {
        log.info("Updating appointment reason: ID {}, Patient email: {}", appointmentId, patientUser.getEmail());


        Optional<Patient> patientOptional = patientRepository.findByUser(patientUser);

        if (patientOptional.isEmpty()) {
            throw new IllegalArgumentException("Doctor profile not found.");
        }

        Patient patient = patientOptional.get();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this appointment.");
        }

        if (appointment.getStatus() != Appointment.Status.Waiting && appointment.getStatus() != Appointment.Status.Booked) {
            throw new IllegalStateException("Cannot update an appointment that is already " + appointment.getStatus());
        }

        appointment.setReason(newReason);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment reason updated successfully: ID {}", updatedAppointment);

        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }
    @Override
    @Transactional
    public AppointmentResponseDTO cancelAppointmentByPatient(Long appointmentId, User patientUser) {
        log.info("Cancelling appointment by patient: ID {}, Patient email: {}", appointmentId, patientUser.getEmail());

        Appointment appointment = findAndVerifyPatientAppointment(appointmentId, patientUser);

        if (appointment.getStatus() != Appointment.Status.Waiting && appointment.getStatus() != Appointment.Status.Booked) {
            throw new IllegalStateException("Cannot cancel an appointment that is already " + appointment.getStatus());
        }

        appointment.setStatus(Appointment.Status.Cancelled);
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment cancelled successfully: ID {}", updatedAppointment);

        String message = "Appointment with " + appointment.getPatient().getName() + " on " + updatedAppointment.getDate() + " has been cancelled by the patient.";
        notificationService.createNotification(updatedAppointment.getDoctor().getUser(), message);

        return AppointmentResponseDTO.fromEntity(updatedAppointment);
    }

    private Appointment findAndVerifyPatientAppointment(Long appointmentId, User patientUser) {
        Optional<Patient> patientOptional = patientRepository.findByUser(patientUser);

        if (patientOptional.isEmpty()) {
            throw new IllegalArgumentException("Doctor profile not found.");
        }

        Patient patient = patientOptional.get();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found."));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new AccessDeniedException("You do not have permission to modify this appointment.");
        }
        return appointment;
    }
}
