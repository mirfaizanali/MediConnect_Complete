package com.example.MediConnect_Backend.dto.responseDTO.availability;

import com.example.MediConnect_Backend.entity.DoctorAvailability;
import lombok.Builder;
import lombok.Data;
import com.example.MediConnect_Backend.enums.SlotStatus;
import java.time.LocalDate;

@Data
@Builder
public class AvailabilityResponse {
    private Long availabilityId;
    private Long doctorId;
    private LocalDate date;
    private String timeSlot;
    private SlotStatus status;

    public static AvailabilityResponse fromEntity(DoctorAvailability doctorAvailability ) {
        return AvailabilityResponse.builder()
                .availabilityId(doctorAvailability.getAvailabilityId())
                .doctorId(doctorAvailability.getDoctor().getId())
                .date(doctorAvailability.getDate())
                .timeSlot(doctorAvailability.getTimeSlot())
                .status(doctorAvailability.getStatus())
                .build();
    }
}