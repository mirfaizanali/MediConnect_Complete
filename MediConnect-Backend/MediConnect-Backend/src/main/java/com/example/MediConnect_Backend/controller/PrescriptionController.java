package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.requestDTO.prescription.CreatePrescriptionRequest;
import com.example.MediConnect_Backend.dto.responseDTO.consultation.ConsultationResponse;
import com.example.MediConnect_Backend.dto.responseDTO.prescription.PrescriptionResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.PrescriptionService;
import com.example.MediConnect_Backend.util.ApiResponse;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/prescription")
@RequiredArgsConstructor
public class PrescriptionController {
    private final UserRepository userRepository;
    private final PrescriptionService prescriptionService;

    @PostMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createPrescription(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @Valid @RequestBody CreatePrescriptionRequest prescriptionDTO) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            PrescriptionResponse createdPrescription = prescriptionService.createPrescription(appointmentId, currentUser, prescriptionDTO);
            return new ResponseEntity<>(ApiResponse.success(createdPrescription), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create consultation"));
        }
    }

    @GetMapping("/{prescriptionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPrescriptionForConsultation(
            @PathVariable Long prescriptionId,
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));
            PrescriptionResponse prescription = prescriptionService.getPrescriptionForAppointment(prescriptionId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(prescription));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get consultation"));
        }
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMyPrescriptions(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

            java.util.List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsForPatient(currentUser);
            return ResponseEntity.ok(ApiResponse.success(prescriptions));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get prescriptions"));
        }
    }

}
