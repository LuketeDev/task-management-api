package com.lukete.task_manager_api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lukete.task_manager_api.dto.request.CreateTaskRequest;
import com.lukete.task_manager_api.dto.request.UpdateTaskRequest;
import com.lukete.task_manager_api.dto.response.TaskResponse;
import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.entity.TaskPriority;
import com.lukete.task_manager_api.entity.TaskStatus;
import com.lukete.task_manager_api.exception.ResourceNotFoundException;
import com.lukete.task_manager_api.mapper.TaskMapper;
import com.lukete.task_manager_api.service.TaskService;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

	private static final String TASKS_URL = "/api/v1/tasks";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TaskService taskService;

	@MockitoBean
	private TaskMapper taskMapper;

	@Test
	void shouldFindAllTasks() throws Exception {

		UUID id = UUID.randomUUID();
		Task task = taskWithId(id);

		TaskResponse response = new TaskResponse(
				task.getId(),
				task.getTitle(),
				task.getDescription(),
				task.getStatus(),
				task.getPriority(),
				task.getDueDate(),
				task.getCreatedAt(),
				task.getUpdatedAt());

		when(taskService.findAll()).thenReturn(List.of(task));
		when(taskMapper.toResponse(task)).thenReturn(response);

		assertThat(task).isNotNull();
		assertThat(taskMapper.toResponse(task)).isNotNull();

		mockMvc.perform(get(TASKS_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value(task.getTitle()))
				.andExpect(jsonPath("$[0].description").value(task.getDescription()))
				.andExpect(jsonPath("$[0].status").value(task.getStatus().name()))
				.andExpect(jsonPath("$[0].priority").value(task.getPriority().name()));

		verify(taskService).findAll();
	}

	@Test
	void shouldCreateTaskWhenRequestIsValid() throws Exception {
		UUID id = UUID.randomUUID();
		Task task = taskWithId(id);
		TaskResponse response = responseFor(id, "Write tests", TaskStatus.PENDING);
		when(taskMapper.toEntity(any(CreateTaskRequest.class))).thenReturn(task);
		when(taskService.create(task)).thenReturn(task);
		when(taskMapper.toResponse(task)).thenReturn(response);

		LocalDate dueDate = LocalDate.now().plusDays(1);

		assertThat(task.getId()).isEqualTo(id);

		mockMvc.perform(post(TASKS_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(
						"""
								{
									"title":"Write tests",
									"description":"Controller tests",
									"priority":"HIGH",
									"dueDate":"%s"
								}
										""".formatted(dueDate)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "http://localhost/api/v1/tasks/" + id))
				.andExpect(jsonPath("$.id").value(id.toString()))
				.andExpect(jsonPath("$.title").value("Write tests"));

		verify(taskService).create(task);
	}

	@Test
	void shouldRejectCreateTaskWithoutTitle() throws Exception {
		mockMvc.perform(post(TASKS_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"description":"Controller tests",
							"priority":"HIGH",
							"dueDate":"2026-09-17"
						}
							"""))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(taskService, taskMapper);
	}

	@Test
	void shouldFindExistingTask() throws Exception {
		UUID id = UUID.randomUUID();
		Task task = taskWithId(id);
		when(taskService.findById(id)).thenReturn(task);
		when(taskMapper.toResponse(task)).thenReturn(responseFor(id, "Write tests", TaskStatus.PENDING));

		mockMvc.perform(get(TASKS_URL + "/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id.toString()))
				.andExpect(jsonPath("$.status").value("PENDING"));

		verify(taskService).findById(id);
	}

	@Test
	void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
		UUID id = UUID.randomUUID();
		when(taskService.findById(id)).thenThrow(new ResourceNotFoundException(id));

		mockMvc.perform(get(TASKS_URL + "/{id}", id))
				.andExpect(status().isNotFound());
	}

	@Test
	void shouldUpdateTaskWhenRequestIsValid() throws Exception {
		UUID id = UUID.randomUUID();
		Task task = taskWithId(id);
		TaskResponse response = responseFor(id, "Updated task", TaskStatus.PENDING);
		when(taskMapper.toEntity(any(UpdateTaskRequest.class))).thenReturn(task);
		when(taskService.update(id, task)).thenReturn(task);
		when(taskMapper.toResponse(task)).thenReturn(response);

		LocalDate dueDate = LocalDate.now().plusDays(1);

		mockMvc.perform(put(TASKS_URL + "/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"title":"Updated task",
							"description":"Updated description",
							"priority":"LOW",
							"dueDate":"%s"
						}
							""".formatted(dueDate)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("Updated task"));

		verify(taskService).update(id, task);
	}

	@Test
	void shouldChangeStatusWhenRequestIsValid() throws Exception {
		UUID id = UUID.randomUUID();
		Task task = taskWithId(id);
		TaskResponse response = responseFor(id, "Write tests", TaskStatus.IN_PROGRESS);
		when(taskService.changeStatus(id, TaskStatus.IN_PROGRESS)).thenReturn(task);
		when(taskMapper.toResponse(task)).thenReturn(response);

		mockMvc.perform(patch(TASKS_URL + "/{id}/status", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"status":"IN_PROGRESS"}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("IN_PROGRESS"));

		verify(taskService).changeStatus(id, TaskStatus.IN_PROGRESS);
	}

	@Test
	void shouldDeleteTask() throws Exception {
		UUID id = UUID.randomUUID();
		doNothing().when(taskService).delete(id);

		mockMvc.perform(delete(TASKS_URL + "/{id}", id))
				.andExpect(status().isNoContent());

		verify(taskService).delete(id);
	}

	private Task taskWithId(UUID id) {
		Task task = new Task(
				"Write documentation",
				"Describe the API",
				TaskStatus.PENDING,
				TaskPriority.HIGH,
				LocalDate.now().plusDays(1));

		try {
			Field field = Task.class.getDeclaredField("id");
			field.setAccessible(true);
			field.set(task, id);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Could not set task id", e);
		}

		return task;
	}

	private TaskResponse responseFor(UUID id, String title, TaskStatus status) {
		return new TaskResponse(
				id,
				title,
				"Controller tests",
				status,
				TaskPriority.HIGH,
				LocalDate.of(2026, 9, 17),
				Instant.parse("2026-09-16T12:00:00Z"),
				Instant.parse("2026-09-16T12:00:00Z"));
	}
}
