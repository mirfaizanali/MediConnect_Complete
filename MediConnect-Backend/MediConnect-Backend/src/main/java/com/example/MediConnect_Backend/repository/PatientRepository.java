package com.example.MediConnect_Backend.repository;

import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient , Long> {
    Optional<Patient> findByUser(User user);
}