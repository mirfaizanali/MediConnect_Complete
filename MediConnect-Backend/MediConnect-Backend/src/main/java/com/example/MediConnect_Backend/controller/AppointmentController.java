package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.requestDTO.appointment.UpdateAppointmentStatusRequest;
import com.example.MediConnect_Backend.dto.responseDTO.appointment.AppointmentResponseDTO;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.AppointmentService;
import com.example.MediConnect_Backend.util.ApiResponse;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.MediConnect_Backend.dto.requestDTO.appointment.BookAppointmentRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @PostMapping("/book")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> bookAppointment(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @Valid @RequestBody BookAppointmentRequest bookingDetails) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            AppointmentResponseDTO appointment = appointmentService.bookAppointment(currentUser, bookingDetails);
            return new ResponseEntity<>(ApiResponse.success(appointment), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to book appointment"));
        }
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getDoctorAppointments(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsForDoctor(currentUser);
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get appointments"));
        }
    }

    @PatchMapping("/{appointmentId}/status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @Valid @RequestBody UpdateAppointmentStatusRequest statusDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentStatus(appointmentId, currentUser, statusDto.getStatus());
            return ResponseEntity.ok(ApiResponse.success(updatedAppointment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to update appointment status"));
        }
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getPatientAppointments(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsForPatient(currentUser);
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get patient appointments"));
        }
    }
}
