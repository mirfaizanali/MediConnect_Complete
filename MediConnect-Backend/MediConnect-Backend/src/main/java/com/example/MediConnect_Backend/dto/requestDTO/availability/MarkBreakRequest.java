package com.example.MediConnect_Backend.dto.requestDTO.availability;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MarkBreakRequest {

    @NotNull(message = "Date is required.")
    private LocalDate date;

    @NotNull(message = "Start time is required.")
    private LocalTime startTime;

    @NotNull(message = "End time is required.")
    private LocalTime endTime;
}

