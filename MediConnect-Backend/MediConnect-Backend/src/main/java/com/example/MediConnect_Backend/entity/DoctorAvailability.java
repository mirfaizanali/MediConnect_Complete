package com.example.MediConnect_Backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.MediConnect_Backend.enums.SlotStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_availability", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"doctor_id", "date", "timeSlot"})
})
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long availabilityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private LocalDate date;

    private String timeSlot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status;
}