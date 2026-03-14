package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.requestDTO.auth.ChangePasswordRequest;
import com.example.MediConnect_Backend.dto.requestDTO.profile.UpdateDoctorProfileRequest;
import com.example.MediConnect_Backend.dto.responseDTO.availability.AvailabilityResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorProfileResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorPublicProfileResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorResponse;
import com.example.MediConnect_Backend.enums.SlotStatus;
import com.example.MediConnect_Backend.exception.ResourceNotFoundException;
import com.example.MediConnect_Backend.repository.DoctorAvailabilityRepository;
import com.example.MediConnect_Backend.repository.DoctorRepository;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.entity.Doctor;
import com.example.MediConnect_Backend.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DoctorServiceImpl implements DoctorService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;


    private final PasswordEncoder passwordEncoder;


    public DoctorServiceImpl(UserRepository userRepository, DoctorRepository doctorRepository, DoctorAvailabilityRepository availabilityRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.doctorRepository=doctorRepository;
        this.availabilityRepository = availabilityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<DoctorResponse> getTopRatedDoctors() {
        log.info("Fetching top 3 rated doctors.");

        Pageable topThree = PageRequest.of(0, 3);
        List<Doctor> doctors = doctorRepository.findTopRatedDoctors(topThree);

        List<DoctorResponse> responseList = new ArrayList<DoctorResponse>();

        for (Doctor doctor : doctors) {
            DoctorResponse dto = convertToPublicDto(doctor);
            responseList.add(dto);
        }

        return responseList;
    }

    private DoctorResponse convertToPublicDto(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialization(doctor.getSpecialization())
                .rating(doctor.getRating())
                .exp(doctor.getExp())
                .build();
    }

    @Override
    public DoctorProfileResponse getDoctorProfile(User currentUser) {
        try {
            if (currentUser == null) {
                throw new IllegalArgumentException("Authenticated user not found.");
            }

            String username = currentUser.getEmail();

            User managedUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));


            Doctor doctor = doctorRepository.findByUser(managedUser)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user: " + managedUser.getEmail()));


            DoctorProfileResponse response = DoctorProfileResponse.fromEntity(doctor);

            log.info("DoctorProfileResponse built successfully for doctor ID {}", doctor.getId());
            return response;

        } catch (Exception e) {
            log.error("Error in getDoctorProfile(): {}", e.getMessage(), e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
    }


    @Override
    @Transactional
    public DoctorProfileResponse updateDoctorProfile(User currentUser, UpdateDoctorProfileRequest profileDto) {
        log.info("updateDoctorProfile called for principal: {}", currentUser == null ? "null" : currentUser.getEmail());
        log.info("Payload: {}", profileDto);

        try {

            if (currentUser == null) {
                throw new IllegalArgumentException("Authenticated user not found.");
            }

            String username = currentUser.getEmail();

            User managedUser = userRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
            log.info("Managed user found: id={}", managedUser.getId());

            Doctor doctor = doctorRepository.findByUser(managedUser)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for user: " + managedUser.getEmail()));            log.info("Doctor before update: id={}, name={}", doctor.getId(), doctor.getName());

            doctor.setName(profileDto.getName());
            doctor.setSpecialization(profileDto.getSpecialization());
            doctor.setExp(profileDto.getExp());
            doctor.setQualification(profileDto.getQualification());

            Doctor updatedDoctor = doctorRepository.saveAndFlush(doctor);
            log.info("Updated profile for doctor ID: {}", updatedDoctor.getId());

            return DoctorProfileResponse.fromEntity(updatedDoctor);
        } catch (Exception e) {
            log.error("Failed to update doctor profile", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void changePassword(User currentUser, ChangePasswordRequest passwordDto) {
        try {
            log.info("=== STARTING PASSWORD CHANGE ===");
            log.info("Received request for user: {}", currentUser.getEmail());

            User managedUser = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> {
                        log.error("User not found in database with ID: {}", currentUser.getId());
                        return new IllegalArgumentException("User not found");
                    });

            log.info("Found managed user: ID={}, Email={}", managedUser.getId(), managedUser.getEmail());

            log.info("Verifying current password...");
            String providedCurrentPassword = passwordDto.getCurrentPassword();

            if (providedCurrentPassword == null || providedCurrentPassword.trim().isEmpty()) {
                log.error("Current password is null or empty");
                throw new BadCredentialsException("Current password is required");
            }

            boolean passwordMatches = passwordEncoder.matches(providedCurrentPassword, managedUser.getPassword());
            log.info("Password match result: {}", passwordMatches);

            if (!passwordMatches) {
                log.warn("Password change failed: Incorrect current password for user {}", managedUser.getEmail());
                throw new BadCredentialsException("Incorrect current password provided.");
            }

            String newPassword = passwordDto.getNewPassword();
            if (newPassword == null || newPassword.trim().isEmpty()) {
                log.error("New password is null or empty");
                throw new IllegalArgumentException("New password is required");
            }

            if (newPassword.length() < 6) {
                log.error("New password too short: {} characters", newPassword.length());
                throw new IllegalArgumentException("New password must be at least 6 characters long");
            }

            log.info("Encoding new password...");
            String newEncodedPassword = passwordEncoder.encode(newPassword);
            managedUser.setPassword(newEncodedPassword);

            log.info("Saving updated user...");
            User savedUser = userRepository.save(managedUser);
            log.info("Password changed successfully for user: {}", savedUser.getEmail());
            log.info("=== PASSWORD CHANGE COMPLETED ===");

        } catch (BadCredentialsException e) {
            log.error("Bad credentials during password change: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Validation error during password change: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during password change: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to change password: " + e.getMessage());
        }
    }

    @Override
    public List<DoctorPublicProfileResponse> getAllDoctorsForPatients() {
        List<Doctor> allDoctors = doctorRepository.findAll();

        return allDoctors.stream().map(doctor -> {
            List<AvailabilityResponse> availableSlots = availabilityRepository
                    .findByDoctorAndDateAfterAndStatus(doctor, LocalDate.now().minusDays(1), SlotStatus.AVAILABLE)
                    .stream()
                    .map(AvailabilityResponse::fromEntity)
                    .collect(Collectors.toList());

            return DoctorPublicProfileResponse.fromEntity(doctor, availableSlots);
        }).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}

