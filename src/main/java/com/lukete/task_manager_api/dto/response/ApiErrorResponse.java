package com.lukete.task_manager_api.dto.response;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String error,
        String message,
        LocalDateTime timestamp) {

}
