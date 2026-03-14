package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.dto.requestDTO.appointment.BookAppointmentRequest;
import com.example.MediConnect_Backend.dto.responseDTO.appointment.AppointmentResponseDTO;
import com.example.MediConnect_Backend.entity.Appointment;
import com.example.MediConnect_Backend.entity.User;

import java.util.List;

public interface AppointmentService {
    AppointmentResponseDTO bookAppointment(User patientUser, BookAppointmentRequest bookingDetails);
    List<AppointmentResponseDTO> getAppointmentsForDoctor(User doctorUser);
    AppointmentResponseDTO updateAppointmentStatus(Long appointmentId, User doctorUser, Appointment.Status newStatus);

    List<AppointmentResponseDTO> getAppointmentsForPatient(User patientUser);
    List<AppointmentResponseDTO> getUpcomingAppointmentsForPatient(User patientUser);
    List<AppointmentResponseDTO> getAppointmentHistoryForPatient(User patientUser);
    AppointmentResponseDTO updateAppointmentReason(Long appointmentId, User patientUser, String newReason);
    AppointmentResponseDTO cancelAppointmentByPatient(Long appointmentId, User patientUser);
}
