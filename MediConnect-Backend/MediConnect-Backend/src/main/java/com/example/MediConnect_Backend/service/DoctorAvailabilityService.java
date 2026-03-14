package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.dto.requestDTO.availability.GenerateScheduleRequest;
import com.example.MediConnect_Backend.dto.requestDTO.availability.MarkBreakRequest;
import com.example.MediConnect_Backend.dto.responseDTO.availability.AvailabilityResponse;
import com.example.MediConnect_Backend.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface DoctorAvailabilityService {
    void generateSchedule(User doctorUser, GenerateScheduleRequest request);
    void markSlotsAsUnavailable(User doctorUser, MarkBreakRequest request);
    List<AvailabilityResponse> getAllAvailability(User doctorUser);
    List<AvailabilityResponse> getAvailabilityForDate(User doctorUser, LocalDate date);
    void clearAvailabilityForDate(User doctorUser, LocalDate date);
    String toggleSlotStatus(User doctorUser, Long availabilityId);

}
