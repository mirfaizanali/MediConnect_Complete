package com.example.MediConnect_Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @ToString.Exclude
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id")
    private DoctorAvailability availability;

    private LocalDate date;

    private String timeSlot;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.Booked;

    private String reason;

    private String specialty;

    public enum Status {
        Booked, Cancelled, Completed, Waiting
    }
}