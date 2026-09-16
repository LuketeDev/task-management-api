package com.lukete.task_manager_api.exception;

import com.lukete.task_manager_api.entity.TaskStatus;

public class InvalidTaskStatusTransitionException extends RuntimeException {
    public InvalidTaskStatusTransitionException(TaskStatus oldStatus, TaskStatus newStatus) {
        super("Invalid status transition from " + oldStatus + " to " + newStatus);
    }

}