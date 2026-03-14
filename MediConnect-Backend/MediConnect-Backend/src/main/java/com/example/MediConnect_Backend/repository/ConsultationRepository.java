package com.example.MediConnect_Backend.repository;


import com.example.MediConnect_Backend.entity.Appointment;
import com.example.MediConnect_Backend.entity.Consultation;
import com.example.MediConnect_Backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByAppointment(Appointment appointment);
    List<Consultation> findByPatientOrderByDateDesc(Patient patient);
}