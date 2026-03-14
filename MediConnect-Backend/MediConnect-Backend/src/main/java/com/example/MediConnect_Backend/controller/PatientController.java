package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.requestDTO.profile.UpdatePatientProfileRequest;
import com.example.MediConnect_Backend.repository.PatientRepository;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.PatientService;
import com.example.MediConnect_Backend.service.impl.PatientServiceImpl;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.MediConnect_Backend.dto.responseDTO.profile.PatientProfileResponse;
import com.example.MediConnect_Backend.dto.responseDTO.history.PatientHistoryResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.util.ApiResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;


@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public PatientController(PatientServiceImpl patientService,UserRepository userRepository,PatientRepository patientRepository){
        this.patientService = patientService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal CustomUserPrinciple principal) { // Changed to <?>
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

            PatientProfileResponse profile = patientService.getPatientProfile(currentUser);

            if (profile == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Could not generate profile"));
            }

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/user")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> updateMyProfile(@AuthenticationPrincipal CustomUserPrinciple principal,@RequestBody UpdatePatientProfileRequest profileDto) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

            PatientProfileResponse profile = patientService.updatePatientProfile(currentUser,profileDto);

            if (profile == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("success", false, "message", "Could not generate profile"));
            }

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMyHistory(@AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "User not authenticated"));
            }
            User currentUser = userRepository.findById(principal.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

             PatientHistoryResponse history = patientService.getPatientHistory(currentUser);
             return ResponseEntity.ok(ApiResponse.success(history));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
