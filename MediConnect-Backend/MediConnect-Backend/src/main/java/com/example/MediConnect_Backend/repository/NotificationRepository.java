package com.example.MediConnect_Backend.repository;

import com.example.MediConnect_Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.example.MediConnect_Backend.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Finds all unread notifications for a user, showing the newest first
    List<Notification> findByUserAndIsReadFalseOrderByIdDesc(User user);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    long countUnreadNotificationsByUserId(@Param("userId") Long userId);


}
