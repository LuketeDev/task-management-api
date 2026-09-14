package com.lukete.task_manager_api.service;

import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.entity.TaskPriority;
import com.lukete.task_manager_api.entity.TaskStatus;
import com.lukete.task_manager_api.exception.ResourceNotFoundException;
import com.lukete.task_manager_api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private TaskService taskService;

	private UUID taskId;

	@BeforeEach
	void setUp() {
		taskId = UUID.randomUUID();
	}

	@Test
	void shouldCreateTaskWithPendingStatusAndDefaultPriority() {
		Task task = new Task("Write documentation", "Describe the API", TaskStatus.COMPLETED, null, LocalDate.now().plusDays(1));
		when(taskRepository.save(task)).thenReturn(task);

		Task createdTask = taskService.create(task);

		assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.PENDING);
		assertThat(createdTask.getPriority()).isEqualTo(TaskPriority.MEDIUM);
		verify(taskRepository).save(task);
	}

	@Test
	void shouldFindExistingTaskById() {
		Task task = taskWithStatus(TaskStatus.PENDING);
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

		Task foundTask = taskService.findById(taskId);

		assertThat(foundTask).isSameAs(task);
		verify(taskRepository).findById(taskId);
	}

	@Test
	void shouldThrowWhenTaskDoesNotExist() {
		when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> taskService.findById(taskId))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining(taskId.toString());
	}

	@Test
	void shouldListAllTasks() {
		List<Task> tasks = List.of(taskWithStatus(TaskStatus.PENDING), taskWithStatus(TaskStatus.IN_PROGRESS));
		when(taskRepository.findAll()).thenReturn(tasks);

		List<Task> result = taskService.findAll();

		assertThat(result).containsExactlyElementsOf(tasks);
		verify(taskRepository).findAll();
	}

	@Test
	void shouldUpdateTaskDetailsWithoutChangingStatus() {
		Task existingTask = taskWithStatus(TaskStatus.IN_PROGRESS);
		Task updatedTask = new Task("Updated title", "Updated description", TaskStatus.CANCELLED, TaskPriority.LOW, LocalDate.now().plusDays(2));
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
		when(taskRepository.save(existingTask)).thenReturn(existingTask);

		Task result = taskService.update(taskId, updatedTask);

		assertThat(result.getTitle()).isEqualTo("Updated title");
		assertThat(result.getDescription()).isEqualTo("Updated description");
		assertThat(result.getPriority()).isEqualTo(TaskPriority.LOW);
		assertThat(result.getDueDate()).isEqualTo(updatedTask.getDueDate());
		assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
		verify(taskRepository).save(existingTask);
	}

	@Test
	void shouldDeleteExistingTask() {
		Task task = taskWithStatus(TaskStatus.PENDING);
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

		taskService.delete(taskId);

		verify(taskRepository).delete(task);
	}

	@Test
	void shouldChangeStatusWhenTransitionIsValid() {
		Task task = taskWithStatus(TaskStatus.PENDING);
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
		when(taskRepository.save(task)).thenReturn(task);

		Task result = taskService.changeStatus(taskId, TaskStatus.IN_PROGRESS);

		assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
		verify(taskRepository).save(task);
	}

	@Test
	void shouldRejectInvalidStatusTransition() {
		Task task = taskWithStatus(TaskStatus.PENDING);
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

		assertThatThrownBy(() -> taskService.changeStatus(taskId, TaskStatus.COMPLETED))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Invalid status transition");

		verify(taskRepository, never()).save(task);
	}

	@Test
	void shouldRejectStatusChangeFromCompletedTask() {
		Task task = taskWithStatus(TaskStatus.COMPLETED);
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

		assertThatThrownBy(() -> taskService.changeStatus(taskId, TaskStatus.CANCELLED))
				.isInstanceOf(IllegalStateException.class);

		verify(taskRepository, never()).save(task);
	}

	@Test
	void shouldRejectStatusChangeFromCancelledTask() {
		Task task = taskWithStatus(TaskStatus.CANCELLED);
		when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

		assertThatThrownBy(() -> taskService.changeStatus(taskId, TaskStatus.IN_PROGRESS))
				.isInstanceOf(IllegalStateException.class);

		verify(taskRepository, never()).save(task);
	}

	private Task taskWithStatus(TaskStatus status) {
		return new Task("Write documentation", "Describe the API", status, TaskPriority.HIGH, LocalDate.now().plusDays(1));
	}
}
