package com.example.MediConnect_Backend.repository;


import com.example.MediConnect_Backend.entity.Appointment;
import com.example.MediConnect_Backend.entity.Doctor;
import com.example.MediConnect_Backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findByDoctor(Doctor doctor);
        List<Appointment> findByPatient(Patient patient);
        List<Appointment> findByPatientOrderByDateDesc(Patient patient);

        List<Appointment> findByPatientAndDateGreaterThanEqualOrderByDateAscTimeSlotAsc(Patient patient, LocalDate date);
        List<Appointment> findByPatientAndDateBeforeOrderByDateDescTimeSlotDesc(Patient patient, LocalDate date);

        @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor = :doctor")
        List<Patient> findDistinctPatientsByDoctor(Doctor doctor);


        List<Appointment> findByDoctorAndPatientOrderByDateDesc(Doctor doctor, Patient patient);


        @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor = :doctor AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :name, '%'))")
        List<Patient> findDistinctPatientsByDoctorAndNameContaining(@Param("doctor") Doctor doctor, @Param("name") String name);

        @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor = :doctor AND LOWER(a.patient.user.email) LIKE LOWER(CONCAT('%', :email, '%'))")
        List<Patient> findDistinctPatientsByDoctorAndEmailContaining(@Param("doctor") Doctor doctor, @Param("email") String email);

        @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor = :doctor AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(a.patient.user.email) LIKE LOWER(CONCAT('%', :email, '%'))")
        List<Patient> findDistinctPatientsByDoctorAndNameContainingAndEmailContaining(@Param("doctor") Doctor doctor, @Param("name") String name, @Param("email") String email);
}