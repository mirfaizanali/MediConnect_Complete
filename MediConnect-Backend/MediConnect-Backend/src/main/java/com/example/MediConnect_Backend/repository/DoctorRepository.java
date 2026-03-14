package com.example.MediConnect_Backend.repository;

import com.example.MediConnect_Backend.entity.Doctor;
import com.example.MediConnect_Backend.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("SELECT d FROM Doctor d ORDER BY d.rating DESC")
    List<Doctor> findTopRatedDoctors(Pageable pageable);

    Optional<Doctor> findByUser(User user);
    Optional<Doctor> findByUserId(Long userId);

    @Query("SELECT d FROM Doctor d WHERE d.user.id = :userId")
    Optional<Doctor> findByUserIdWithQuery(@Param("userId") Long userId);

    Optional<Doctor> findByUser_Id(Long UserId);
}