package com.example.MediConnect_Backend.dto.responseDTO.profile;

import lombok.Builder;
import lombok.Data;
import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.enums.Gender;

@Data
@Builder
public class PatientForDoctorResponse {
    private Long patientId;
    private String name;
    private int age;
    private Gender gender;

    public static PatientForDoctorResponse fromEntity(Patient patient) {
        return PatientForDoctorResponse.builder()
                .patientId(patient.getId())
                .name(patient.getName())
                .age(patient.getAge())
                .gender(patient.getGender())
                .build();
    }
}
