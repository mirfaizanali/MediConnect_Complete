package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.requestDTO.auth.LoginRequest;
import lombok.RequiredArgsConstructor;
import com.example.MediConnect_Backend.dto.requestDTO.auth.RegisterDoctorRequest;
import com.example.MediConnect_Backend.dto.requestDTO.auth.RegisterPatientRequest;
import com.example.MediConnect_Backend.entity.Doctor;
import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.enums.Role;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.DoctorRepository;
import com.example.MediConnect_Backend.repository.PatientRepository;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;

import java.util.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    @Autowired
    private JwtUtil jwtService;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginDto) {

        Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());

        if (userOptional.isPresent() && passwordEncoder.matches(loginDto.getPassword(), userOptional.get().getPassword())) {
            UserDetails userDetails = userOptional.get();

            String token = jwtService.generateToken(userOptional.get().getEmail(), userOptional.get().getRole().name(), userOptional.get().getId());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userOptional.get().getId());
            userInfo.put("email", userOptional.get().getEmail());
            userInfo.put("role", userOptional.get().getRole().name());
            userInfo.put("name",userOptional.get().getUsername());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "User signed in successfully!");
            responseBody.put("status", true);
            responseBody.put("token", token);
            responseBody.put("user", userInfo);

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Invalid email or password!");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register-patient")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody RegisterPatientRequest registerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Validation failed");
            map.put("errors", new HashMap<>());
            for (FieldError error : bindingResult.getFieldErrors()) {
                ((Map<String, Object>) map.get("errors")).put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Email is already taken!");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(Role.ROLE_PATIENT)
                .name(registerDto.getName())
                .build();

        User savedUser = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(savedUser)
                .name(registerDto.getName())
                .age(registerDto.getAge())
                .bloodGroup(registerDto.getBloodGroup())
                .phoneNumber(registerDto.getPhoneNumber())
                .address(registerDto.getAddress())
                .gender(registerDto.getGender())
                .build();

        patientRepository.save(patient);

        Map<String, Object> map = new HashMap<>();
        map.put("message", "Patient registered successfully!");
        map.put("status", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PostMapping("/register-doctor")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody RegisterDoctorRequest registerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Validation failed");
            map.put("errors", new HashMap<>());
            for (FieldError error : bindingResult.getFieldErrors()) {
                ((Map<String, Object>) map.get("errors")).put(error.getField(), error.getDefaultMessage());
            }
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Email is already taken!");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(Role.ROLE_DOCTOR)
                .name(registerDto.getName())
                .build();

        User savedUser = userRepository.save(user);

        Doctor doctor = Doctor.builder()
                .user(savedUser)
                .name(registerDto.getName())
                .exp(registerDto.getExp())
                .qualification(registerDto.getQualification())
                .specialization(registerDto.getSpecialization())
                .build();


        doctorRepository.save(doctor);

        Map<String, Object> map = new HashMap<>();
        map.put("message", "Doctor registered successfully!");
        map.put("status", true);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        String token = jwtService.getTokenFromHeader(authorizationHeader);

        if (token == null) {
            return buildErrorResponse("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtService.validateToken(token)) {
            return buildErrorResponse("Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }

        String username = jwtService.extractUsername(token);
        Optional<User> userOptional = userRepository.findByEmail(username);

        if (userOptional.isEmpty()) {
            return buildErrorResponse("User not found", HttpStatus.UNAUTHORIZED);
        }

        User user = userOptional.get();

        String newToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getId()
        );

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().name());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Token refreshed successfully!");
        responseBody.put("status", true);
        responseBody.put("token", newToken);
        responseBody.put("user", userInfo);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("status", false);
        return new ResponseEntity<>(error, status);
    }
}