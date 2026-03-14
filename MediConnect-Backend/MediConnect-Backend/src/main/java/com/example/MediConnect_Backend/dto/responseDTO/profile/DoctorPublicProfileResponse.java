package com.example.MediConnect_Backend.dto.responseDTO.profile;

import com.example.MediConnect_Backend.dto.responseDTO.availability.AvailabilityResponse;
import lombok.Builder;
import lombok.Data;
//import com.example.MediConnect_Backend.dto.responseDTO.availability.AvailabilityResponse;
import com.example.MediConnect_Backend.entity.Doctor;

import java.util.List;

@Data
@Builder
public class DoctorPublicProfileResponse {
    private Long id;
    private String name;
    private String specialization;
    private int exp;
    private String qualification;
    private float rating;
    private List<AvailabilityResponse> availability;

    public static DoctorPublicProfileResponse fromEntity(Doctor doctor, List<AvailabilityResponse> availability) {
        return DoctorPublicProfileResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialization(doctor.getSpecialization())
                .exp(doctor.getExp())
                .qualification(doctor.getQualification())
                .rating(doctor.getRating())
                .availability(availability)
                .build();
    }
}
