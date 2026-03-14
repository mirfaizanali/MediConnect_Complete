package com.example.MediConnect_Backend.dto.responseDTO.notification;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationCountResponse {
    private long unreadCount;
    private boolean hasNewNotifications;
}
