package com.example.MediConnect_Backend.dto.requestDTO.profile;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.example.MediConnect_Backend.entity.Patient;
import com.example.MediConnect_Backend.enums.Gender;

@Data
public class UpdatePatientProfileRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;

    private int age;

    @NotBlank(message = "Blood group cannot be blank")
    private String bloodGroup;

    private long phoneNumber;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotNull(message = "Gender is required")
    private Gender gender;
}