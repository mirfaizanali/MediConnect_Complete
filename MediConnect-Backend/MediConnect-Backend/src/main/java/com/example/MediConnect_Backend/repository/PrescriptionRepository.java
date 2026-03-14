package com.example.MediConnect_Backend.repository;

import com.example.MediConnect_Backend.entity.Consultation;
import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByConsultation(Consultation consultation);
    List<Prescription> findByPatientOrderByDateDesc(Patient patient);
}
