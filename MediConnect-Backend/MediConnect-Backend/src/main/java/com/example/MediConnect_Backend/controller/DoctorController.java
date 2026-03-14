package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.repository.DoctorRepository;
import com.example.MediConnect_Backend.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.MediConnect_Backend.dto.requestDTO.auth.ChangePasswordRequest;
import com.example.MediConnect_Backend.dto.requestDTO.profile.UpdateDoctorProfileRequest;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorProfileResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorPublicProfileResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.util.ApiResponse;
import com.example.MediConnect_Backend.service.impl.DoctorServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorServiceImpl doctorService,UserRepository userRepository,DoctorRepository doctorRepository){
        this.doctorService = doctorService;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getTopRatedDoctors() {
        List<DoctorResponse> topDoctors = doctorService.getTopRatedDoctors();
        return ResponseEntity.ok(ApiResponse.success(topDoctors));
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        Optional<User> user = Optional.ofNullable(doctorService.getUserById(userId));
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            DoctorProfileResponse profile = doctorService.getDoctorProfile(currentUser);
            return ResponseEntity.ok(profile);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Failed to get profile"));
        }
    }


    @PutMapping("/user")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @Valid @RequestBody UpdateDoctorProfileRequest profileDto) {
        try {

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            DoctorProfileResponse updatedProfile = doctorService.updateDoctorProfile(currentUser, profileDto);
            return ResponseEntity.ok(ApiResponse.success(updatedProfile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to update doctor profile"));
        }
    }


    @PutMapping("/user/change-password")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserPrinciple principal,
            @Valid @RequestBody ChangePasswordRequest passwordDto) {
        try {

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + userId));

            doctorService.changePassword(currentUser, passwordDto);

            return ResponseEntity.ok(ApiResponse.success("Password changed successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to change password"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DoctorPublicProfileResponse>>> getAllDoctors() {
        try {
            List<DoctorPublicProfileResponse> doctors = doctorService.getAllDoctorsForPatients();
            return ResponseEntity.ok(ApiResponse.success(doctors));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to fetch doctors"));
        }
    }


}