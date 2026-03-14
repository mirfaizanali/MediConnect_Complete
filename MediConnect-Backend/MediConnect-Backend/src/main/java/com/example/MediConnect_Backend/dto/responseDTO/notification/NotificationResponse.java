package com.example.MediConnect_Backend.dto.responseDTO.notification;

import lombok.Builder;
import lombok.Data;
import com.example.MediConnect_Backend.entity.Notification;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private Boolean isRead;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .build();
    }
}
