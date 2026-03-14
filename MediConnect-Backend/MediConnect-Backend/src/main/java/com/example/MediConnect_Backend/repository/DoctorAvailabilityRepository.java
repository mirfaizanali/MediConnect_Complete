package com.example.MediConnect_Backend.repository;

import jakarta.persistence.LockModeType;
import com.example.MediConnect_Backend.entity.Doctor;
import com.example.MediConnect_Backend.entity.DoctorAvailability;
import com.example.MediConnect_Backend.enums.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorAndDate(Doctor doctor, LocalDate date);

    List<DoctorAvailability> findByDoctorOrderByDateAscTimeSlotAsc(Doctor doctor);

    @Modifying
    @Query("UPDATE DoctorAvailability da SET da.status = :status WHERE da.doctor = :doctor AND da.date = :date AND da.timeSlot IN :timeSlots")
    void updateSlotStatusForTimeRange(@Param("doctor") Doctor doctor,
                                      @Param("date") LocalDate date,
                                      @Param("timeSlots") List<String> timeSlots,
                                      @Param("status") SlotStatus status);

    List<DoctorAvailability> findByDoctorAndDateAfterAndStatus(Doctor doctor, LocalDate date, SlotStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT da FROM DoctorAvailability da WHERE da.availabilityId = :availabilityId AND da.status = :status")
    Optional<DoctorAvailability> findByAvailabilityIdAndStatus(@Param("availabilityId") Long availabilityId,
                                                               @Param("status") SlotStatus status);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT da FROM DoctorAvailability da WHERE da.availabilityId = :availabilityId")
    Optional<DoctorAvailability> findByIdWithLock(@Param("availabilityId") Long availabilityId);

}

