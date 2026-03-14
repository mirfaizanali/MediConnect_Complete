package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.dto.requestDTO.auth.ChangePasswordRequest;
import com.example.MediConnect_Backend.dto.requestDTO.profile.UpdateDoctorProfileRequest;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorProfileResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorPublicProfileResponse;
import com.example.MediConnect_Backend.dto.responseDTO.profile.DoctorResponse;
import com.example.MediConnect_Backend.entity.User;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.List;

public interface DoctorService extends UserDetails {

    List<DoctorResponse> getTopRatedDoctors();

    DoctorProfileResponse getDoctorProfile(User currentUser);

    DoctorProfileResponse updateDoctorProfile(User currentUser, UpdateDoctorProfileRequest profileDto);

    User getUserById(Long userId);

    void changePassword(User currentUser, ChangePasswordRequest passwordDto);

    List<DoctorPublicProfileResponse> getAllDoctorsForPatients();

}
