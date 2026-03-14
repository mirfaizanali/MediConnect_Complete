package com.example.MediConnect_Backend.service;

import com.example.MediConnect_Backend.dto.responseDTO.notification.NotificationCountResponse;
import com.example.MediConnect_Backend.dto.responseDTO.notification.NotificationResponse;
import com.example.MediConnect_Backend.entity.User;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, String message);
    List<NotificationResponse> getUnreadNotificationsForUser(User user);
    NotificationResponse markAsRead(Long notificationId, User currentUser);
    List<NotificationResponse> markAllAsRead(User currentUser);
    NotificationCountResponse countUnreadNotifications(User currentUser);
}
