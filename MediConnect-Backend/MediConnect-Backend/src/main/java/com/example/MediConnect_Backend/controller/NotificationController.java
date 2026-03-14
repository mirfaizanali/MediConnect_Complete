package com.example.MediConnect_Backend.controller;

import com.example.MediConnect_Backend.dto.responseDTO.notification.NotificationCountResponse;
import com.example.MediConnect_Backend.dto.responseDTO.notification.NotificationResponse;
import com.example.MediConnect_Backend.entity.User;
import com.example.MediConnect_Backend.repository.UserRepository;
import com.example.MediConnect_Backend.service.NotificationService;
import com.example.MediConnect_Backend.util.ApiResponse;
import com.example.MediConnect_Backend.util.CustomUserPrinciple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getMyNotifications(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            List<NotificationResponse> notifications = notificationService.getUnreadNotificationsForUser(currentUser);
            log.info("Found {} notifications for user: {}", notifications.size(), currentUser);

            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (IllegalArgumentException e) {
            log.error("Error getting notifications: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error getting notifications: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to get notifications"));
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            NotificationResponse notification = notificationService.markAsRead(id, currentUser);
            return ResponseEntity.ok(ApiResponse.success(notification));
        } catch (IllegalArgumentException e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error marking notification as read: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to mark notification as read"));
        }
    }

    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(
            @AuthenticationPrincipal CustomUserPrinciple principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Authentication failed"));
            }

            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            List<NotificationResponse> notifications = notificationService.markAllAsRead(currentUser);
            log.info("Marked {} notifications as read for user: {}", notifications.size(), currentUser);

            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (IllegalArgumentException e) {
            log.error("Error marking all notifications as read: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error marking all notifications as read: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to mark all notifications as read"));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<NotificationCountResponse>> getUnreadNotificationCount(@AuthenticationPrincipal CustomUserPrinciple principal) {

        try {
            Long userId = principal.getUserId();

            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + userId));

            NotificationCountResponse countResponse = notificationService.countUnreadNotifications(currentUser);
            log.info("Service returned unread count: {}", countResponse.getUnreadCount());

            return ResponseEntity.ok(ApiResponse.success(countResponse));

        } catch (IllegalArgumentException e) {
            log.error("Error in getUnreadNotificationCount: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
