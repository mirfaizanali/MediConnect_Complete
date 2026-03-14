package com.example.MediConnect_Backend.service;


import com.example.MediConnect_Backend.dto.requestDTO.auth.RegisterPatientRequest;
import com.example.MediConnect_Backend.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    @Transactional
    User registerUser(@Valid RegisterPatientRequest userRequestDTO);

    @Transactional(readOnly = true)
    User getUserById(Long userId);

    @Transactional(readOnly = true)
    List<User> getAllUsers();

    @Transactional
    User updateUser(Long userId, User updatedUser);

    @Transactional
    void deleteUser(Long userId);
}

