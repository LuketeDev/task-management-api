package com.lukete.task_manager_api.service;

import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.entity.TaskPriority;
import com.lukete.task_manager_api.entity.TaskStatus;
import com.lukete.task_manager_api.exception.InvalidTaskStatusTransition;
import com.lukete.task_manager_api.exception.ResourceNotFoundException;
import com.lukete.task_manager_api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;

	public Task create(Task task) {
		task.changeStatus(TaskStatus.PENDING);

		if (task.getPriority() == null) {
			task.updateDetails(task.getTitle(), task.getDescription(), TaskPriority.MEDIUM, task.getDueDate());
		}

		return taskRepository.save(task);
	}

	public Task findById(UUID id) {
		return taskRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));
	}

	public List<Task> findAll() {
		return taskRepository.findAll();
	}

	public Task update(UUID id, Task updatedTask) {
		Task existingTask = findById(id);
		TaskPriority priority = updatedTask.getPriority() != null
				? updatedTask.getPriority()
				: existingTask.getPriority();

		existingTask.updateDetails(
				updatedTask.getTitle(),
				updatedTask.getDescription(),
				priority,
				updatedTask.getDueDate());

		return taskRepository.save(existingTask);
	}

	public Task changeStatus(UUID id, TaskStatus newStatus) {
		Task task = findById(id);

		if (!isValidTransition(task.getStatus(), newStatus)) {
			throw new InvalidTaskStatusTransition(task.getStatus(), newStatus);
		}

		task.changeStatus(newStatus);
		return taskRepository.save(task);
	}

	public void delete(UUID id) {
		Task task = findById(id);
		taskRepository.delete(task);
	}

	private boolean isValidTransition(TaskStatus currentStatus, TaskStatus newStatus) {
		return switch (currentStatus) {
			case PENDING -> newStatus == TaskStatus.IN_PROGRESS || newStatus == TaskStatus.CANCELLED;
			case IN_PROGRESS -> newStatus == TaskStatus.COMPLETED || newStatus == TaskStatus.CANCELLED;
			case COMPLETED, CANCELLED -> false;
		};
	}
}
