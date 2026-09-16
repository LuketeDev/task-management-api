package com.lukete.task_manager_api.mapper;

import com.lukete.task_manager_api.dto.request.CreateTaskRequest;
import com.lukete.task_manager_api.dto.request.UpdateTaskRequest;
import com.lukete.task_manager_api.dto.response.TaskResponse;
import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.entity.TaskPriority;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskMapper {

	public Task toEntity(CreateTaskRequest request) {
		return toTask(request.getTitle(), request.getDescription(), request.getPriority(), request.getDueDate());
	}

	public Task toEntity(UpdateTaskRequest request) {
		return toTask(request.getTitle(), request.getDescription(), request.getPriority(), request.getDueDate());
	}

	public TaskResponse toResponse(Task task) {
		return new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getPriority(),
				task.getDueDate(),
				task.getCreatedAt(),
				task.getUpdatedAt()
		);
	}

	private Task toTask(String title, String description, TaskPriority priority, LocalDate dueDate) {
		return new Task(title, description, null, priority, dueDate);
	}
}
