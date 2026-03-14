package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.requestDTO.appointment.UpdateAppointmentRequest;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patient-appointments")
@RequiredArgsConstructor
public class PatientAppointmentController {
    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @GetMapping("/upcoming")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getUpcomingAppointments(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingAppointmentsForPatient(currentUser);
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get upcoming appointments"));
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getAppointmentHistory(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentHistoryForPatient(currentUser);
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get appointment history"));
        }
    }


    @PatchMapping("/{appointmentId}/reason")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> updateAppointmentReason(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @Valid @RequestBody UpdateAppointmentRequest updateDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentReason(appointmentId, currentUser, updateDto.getReason());
            return ResponseEntity.ok(ApiResponse.success(updatedAppointment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to update appointment reason"));
        }
    }

    @PatchMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            AppointmentResponseDTO cancelledAppointment = appointmentService.cancelAppointmentByPatient(appointmentId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(cancelledAppointment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to cancel appointment"));
        }
    }

}
