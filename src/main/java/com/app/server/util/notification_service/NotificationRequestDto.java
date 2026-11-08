package com.app.server.util.notification_service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequestDto {

    private NotificationType type;

    @NotBlank(message = "recipient is required")
    @Size(max = 254, message = "recipient is too long")
    private String recipient;

    @Size(max = 200, message = "subject must be at most 200 characters")
    private String subject;

    @NotBlank(message = "message is required")
    @Size(max = 5000, message = "message is too long")
    private String message;
}
