package com.lukete.task_manager_api.dto.response;

import com.lukete.task_manager_api.entity.TaskPriority;
import com.lukete.task_manager_api.entity.TaskStatus;
import lombok.Value;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Value
public class TaskResponse {

	UUID id;
	String title;
	String description;
	TaskStatus status;
	TaskPriority priority;
	LocalDate dueDate;
	Instant createdAt;
	Instant updatedAt;
}
