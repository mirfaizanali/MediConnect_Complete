package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.requestDTO.consultation.CreateConsultationRequest;
import com.example.MediConnect_Backend.dto.responseDTO.consultation.ConsultationResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.ConsultationService;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.MediConnect_Backend.util.ApiResponse;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/patient-consultations")
@RestController
public class PatientConsultationController {
    private final UserRepository userRepository;
    private final ConsultationService consultationService;

    public PatientConsultationController(UserRepository userRepository,ConsultationService consultationService){
        this.userRepository = userRepository;
        this.consultationService = consultationService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getAllMyConsultations(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));
            List<ConsultationResponse> consultations = consultationService.getAllConsultationsForPatient(currentUser);

            return ResponseEntity.ok(ApiResponse.success(consultations));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get consultations"));
        }
    }

    @PostMapping("/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createConsultation(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @Valid @RequestBody CreateConsultationRequest consultationDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            ConsultationResponse createdConsultation = consultationService.createConsultation(appointmentId, currentUser, consultationDto);
            return new ResponseEntity<>(ApiResponse.success(createdConsultation), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to create consultation"));
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getConsultationForAppointment(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));
            ConsultationResponse consultation = consultationService.getConsultationForAppointment(appointmentId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(consultation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get consultation"));
        }
    }
}
