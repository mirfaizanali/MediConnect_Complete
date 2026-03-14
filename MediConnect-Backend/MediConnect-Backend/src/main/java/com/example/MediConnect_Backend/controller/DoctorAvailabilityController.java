package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.requestDTO.availability.GenerateScheduleRequest;
import com.example.MediConnect_Backend.dto.requestDTO.availability.MarkBreakRequest;
import com.example.MediConnect_Backend.dto.responseDTO.availability.AvailabilityResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.DoctorAvailabilityService;
import com.example.MediConnect_Backend.service.impl.DoctorAvailabilityServiceImpl;
import com.example.MediConnect_Backend.util.ApiResponse;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors/availability")
public class DoctorAvailabilityController {
private final UserRepository userRepository;
private final DoctorAvailabilityService doctorService;

public DoctorAvailabilityController(UserRepository userRepository,DoctorAvailabilityServiceImpl doctorService){
    this.userRepository = userRepository;
    this.doctorService = doctorService;
}

    @PostMapping("/generate-schedule")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> generateSchedule(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @RequestBody GenerateScheduleRequest request) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            doctorService.generateSchedule(currentUser, request);
            return ResponseEntity.ok(ApiResponse.success("Schedule generated successfully for " + request.getDate()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to get profile"));
        }
    }

    @PutMapping("/mark-break")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> markBreak(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @RequestBody MarkBreakRequest request) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            doctorService.markSlotsAsUnavailable(currentUser, request);
            return ResponseEntity.ok(ApiResponse.success("Break time marked successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to mark break"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAvailability(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            // Fetch the user object from repository
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));


            List<AvailabilityResponse> response = doctorService.getAllAvailability(currentUser);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get availability"));
        }
    }




    @GetMapping("/date/{date}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getAvailabilityForDate(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            // Fetch the user object from repository
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            List<AvailabilityResponse> response = doctorService.getAvailabilityForDate(currentUser, date);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get availability for date"));
        }
    }

    @DeleteMapping("/clear/{date}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> clearAvailabilityForDate(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            // Fetch the user object from repository
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            doctorService.clearAvailabilityForDate(currentUser, date);
            return ResponseEntity.ok(ApiResponse.success("Availability cleared for " + date));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to clear availability"));
        }
    }

    @PatchMapping("/{availabilityId}/toggle-status")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> toggleSlotStatus(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @PathVariable Long availabilityId) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false));
            }

            Long userId = principal.getUserId();
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            // Call a new toggle method
            String newStatus = doctorService.toggleSlotStatus(currentUser, availabilityId);

            return ResponseEntity.ok(ApiResponse.success("Slot is now " + newStatus));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    }

