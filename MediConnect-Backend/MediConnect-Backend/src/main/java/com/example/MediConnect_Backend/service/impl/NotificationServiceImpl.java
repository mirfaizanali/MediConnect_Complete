package com.example.MediConnect_Backend.service.impl;

import com.example.MediConnect_Backend.dto.responseDTO.notification.NotificationCountResponse;
import com.example.MediConnect_Backend.dto.responseDTO.notification.NotificationResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.NotificationRepository;
import com.example.MediConnect_Backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import com.example.MediConnect_Backend.entity.Notification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(User user, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

    }

    @Override
    public List<NotificationResponse> getUnreadNotificationsForUser(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByIdDesc(user)
                .stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));

        // Security check: ensure the user owns this notification
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this notification.");
        }

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return NotificationResponse.fromEntity(updatedNotification);
    }

    @Override
    @Transactional
    public List<NotificationResponse> markAllAsRead(User currentUser) {
        List<Notification> unreadNotifications = notificationRepository.findByUserAndIsReadFalseOrderByIdDesc(currentUser);

        if (unreadNotifications.isEmpty()) {
            return List.of(); // Return an empty list if there's nothing to do
        }

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }

        List<Notification> updatedNotifications = notificationRepository.saveAll(unreadNotifications);

        return updatedNotifications.stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationCountResponse countUnreadNotifications(User currentUser) {
        long unreadCount = notificationRepository.countUnreadNotificationsByUserId(currentUser.getId());

        boolean hasNew = unreadCount > 0;
        return new NotificationCountResponse(unreadCount, hasNew);
    }
}
