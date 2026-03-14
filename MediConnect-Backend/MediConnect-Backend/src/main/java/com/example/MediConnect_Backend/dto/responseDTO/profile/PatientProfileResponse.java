package com.example.MediConnect_Backend.dto.responseDTO.profile;


import lombok.Builder;
import lombok.Data;
import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.enums.Gender;

@Data
@Builder
public class PatientProfileResponse {
    private Long patientId;
    private String name;
    private String email;
    private int age;
    private String bloodGroup;
    private long phoneNumber;
    private String address;
    private Gender gender;

    public static PatientProfileResponse fromEntity(Patient patient) {
        return PatientProfileResponse.builder()
                .patientId(patient.getId())
                .name(patient.getName())
                .email(patient.getUser().getEmail())
                .age(patient.getAge())
                .bloodGroup(patient.getBloodGroup())
                .phoneNumber(patient.getPhoneNumber())
                .address(patient.getAddress())
                .gender(patient.getGender())
                .build();
    }
}
