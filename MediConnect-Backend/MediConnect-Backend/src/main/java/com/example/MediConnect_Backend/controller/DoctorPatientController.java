package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.responseDTO.history.PatientHistoryResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.PatientForDoctorResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.DoctorPatientService;
import com.example.MediConnect_Backend.util.ApiResponse;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor-panel")
@RequiredArgsConstructor
public class DoctorPatientController {
    private final DoctorPatientService doctorPatientService;
    private final UserRepository userRepository;

    @GetMapping("/patients")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getMyPatients(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            List<PatientForDoctorResponse> patients = doctorPatientService.getAssociatedPatients(currentUser);

            return ResponseEntity.ok(ApiResponse.success(patients));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get associated patients"));
        }
    }


    @GetMapping("/patients/{patientId}/history")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getPatientHistory(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @PathVariable Long patientId) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            PatientHistoryResponse history = doctorPatientService.getPatientHistory(currentUser, patientId);

            return ResponseEntity.ok(ApiResponse.success(history));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get patient history"));
        }
    }

    @GetMapping("/patients/search")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> searchPatients(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            List<PatientForDoctorResponse> patients = doctorPatientService.searchPatients(currentUser, name, email);

            return ResponseEntity.ok(ApiResponse.success(patients));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to search patients"));
        }
    }
}
