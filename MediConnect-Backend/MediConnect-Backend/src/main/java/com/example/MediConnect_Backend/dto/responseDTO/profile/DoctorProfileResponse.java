package com.example.MediConnect_Backend.dto.responseDTO.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.MediConnect_Backend.entity.Doctor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String specialization;
    private int exp;
    private String qualification;
    private float rating;

    public static DoctorProfileResponse fromEntity(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor entity cannot be null");
        }

        String email = null;
        try {
            if (doctor.getUser() != null) {
                email = doctor.getUser().getEmail();
            }
        } catch (Exception e) {
            // Defensive catch in case of LazyInitializationException
            email = "N/A";
        }

        return DoctorProfileResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .email(email)
                .specialization(doctor.getSpecialization())
                .exp(doctor.getExp())
                .qualification(doctor.getQualification())
                .rating(doctor.getRating())
                .build();
    }
}
