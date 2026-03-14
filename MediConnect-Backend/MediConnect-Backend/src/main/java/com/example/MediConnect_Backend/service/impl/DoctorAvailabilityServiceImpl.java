package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.requestDTO.availability.GenerateScheduleRequest;
import com.example.MediConnect_Backend.dto.requestDTO.availability.MarkBreakRequest;
import com.example.MediConnect_Backend.dto.responseDTO.availability.AvailabilityResponse;
import com.example.MediConnect_Backend.entity.Doctor;
import com.example.MediConnect_Backend.entity.DoctorAvailability;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.DoctorAvailabilityRepository;
import com.example.MediConnect_Backend.repository.DoctorRepository;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.DoctorAvailabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.MediConnect_Backend.enums.SlotStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {


    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;


    public DoctorAvailabilityServiceImpl(DoctorAvailabilityRepository availabilityRepository, DoctorRepository doctorRepository, UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void generateSchedule(User doctorUser, GenerateScheduleRequest request) {
        Doctor doctor = findDoctorByUser(doctorUser);

        List<DoctorAvailability> existingSlots = availabilityRepository.findByDoctorAndDate(doctor, request.getDate());
        if (!existingSlots.isEmpty()) {
            availabilityRepository.deleteAll(existingSlots);
            log.info("Cleared {} existing slots for date: {}", existingSlots.size(), request.getDate());
        }

        List<DoctorAvailability> newSlots = new ArrayList<>();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        int slotDuration = 30;

        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(slotDuration)) {
            newSlots.add(DoctorAvailability.builder()
                    .doctor(doctor)
                    .date(request.getDate())
                    .timeSlot(time.format(DateTimeFormatter.ofPattern("HH:mm")))
                    .status(SlotStatus.AVAILABLE)
                    .build());
        }

        availabilityRepository.saveAll(newSlots);
        log.info("Generated {} slots for Doctor ID {} on {}", newSlots.size(), doctor.getId(), request.getDate());
    }

    @Override
    @Transactional
    public void markSlotsAsUnavailable(User doctorUser, MarkBreakRequest request) {
        try {
            log.info("=== START markSlotsAsUnavailable ===");
            log.info("Doctor email: {}, Date: {}, From: {}, To: {}",
                    doctorUser.getEmail(), request.getDate(), request.getStartTime(), request.getEndTime());

            Doctor doctor = findDoctorByUser(doctorUser);

            List<String> timeSlots = calculateSlotsInRange(request.getStartTime(), request.getEndTime());
            log.info("Calculated {} slots between {} and {}", timeSlots.size(), request.getStartTime(), request.getEndTime());

            availabilityRepository.updateSlotStatusForTimeRange(
                    doctor,
                    request.getDate(),
                    timeSlots,
                    SlotStatus.UNAVAILABLE
            );

            log.info("Marked {} slots as UNAVAILABLE for Doctor ID {} on {}",
                    timeSlots.size(), doctor.getId(), request.getDate());
            log.info("=== END markSlotsAsUnavailable ===");
        } catch (Exception e) {
            log.error("Error marking multiple slots unavailable: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AvailabilityResponse> getAllAvailability(User doctorUser) {
        Doctor doctor = findDoctorByUser(doctorUser);

        List<DoctorAvailability> availabilities = availabilityRepository.findByDoctorOrderByDateAscTimeSlotAsc(doctor);

        List<AvailabilityResponse> responses = new ArrayList<>();

        for (DoctorAvailability availability : availabilities) {
            responses.add(AvailabilityResponse.fromEntity(availability));
        }

        return responses;
    }

    public List<AvailabilityResponse> getAvailabilityForDate(User doctorUser, LocalDate date) {
        Doctor doctor = findDoctorByUser(doctorUser);

        List<DoctorAvailability> availabilityEntities = availabilityRepository.findByDoctorAndDate(doctor, date);

        List<AvailabilityResponse> responseList = new ArrayList<AvailabilityResponse>();

        for (DoctorAvailability entity : availabilityEntities) {
            responseList.add(AvailabilityResponse.fromEntity(entity));
        }

        return responseList;
    }

    @Transactional
    public void clearAvailabilityForDate(User doctorUser, LocalDate date) {
        Doctor doctor = findDoctorByUser(doctorUser);
        List<DoctorAvailability> slots = availabilityRepository.findByDoctorAndDate(doctor, date);
        if (!slots.isEmpty()) {
            availabilityRepository.deleteAll(slots);
            log.info("Cleared {} slots for Doctor ID {} on {}", slots.size(), doctor.getId(), date);
        }
    }


    @Transactional
    @Override
    public String toggleSlotStatus(User doctorUser, Long availabilityId) {
        Doctor doctor = findDoctorByUser(doctorUser);

        DoctorAvailability slot = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));


        if (!slot.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Unauthorized to modify this slot");
        }


        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new RuntimeException("Cannot toggle a slot that is already booked by a patient");
        }

        SlotStatus newStatus = (slot.getStatus() == SlotStatus.AVAILABLE)
                ? SlotStatus.UNAVAILABLE
                : SlotStatus.AVAILABLE;

        slot.setStatus(newStatus);
        availabilityRepository.save(slot);

        return newStatus.name();
    }


    private Doctor findDoctorByUser(User user) {
        User managedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + user.getEmail()));
        return doctorRepository.findByUser(managedUser)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found for the current user."));
    }

    private List<String> calculateSlotsInRange(LocalTime startTime, LocalTime endTime) {
        List<String> slots = new ArrayList<>();
        int slotDuration = 30;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(slotDuration)) {
            slots.add(time.format(formatter));
        }
        return slots;
    }
}
