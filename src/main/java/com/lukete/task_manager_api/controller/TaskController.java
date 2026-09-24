package com.lukete.task_manager_api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lukete.task_manager_api.dto.request.CreateTaskRequest;
import com.lukete.task_manager_api.dto.request.UpdateTaskRequest;
import com.lukete.task_manager_api.dto.request.UpdateTaskStatusRequest;
import com.lukete.task_manager_api.dto.response.TaskResponse;
import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.mapper.TaskMapper;
import com.lukete.task_manager_api.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tasks", description = "Operations for managing tasks")
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;
	private final TaskMapper taskMapper;

	@Operation(summary = "Create a task", description = "Creates a new task with PENDING status.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Task created"),
	})
	@PostMapping
	public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
		Task createdTask = taskService.create(taskMapper.toEntity(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(createdTask.getId())
				.toUri();

		return ResponseEntity.created(location).body(taskMapper.toResponse(createdTask));
	}

	@Operation(summary = "List tasks", description = "Finds and lists all tasks")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Tasks listed"),
	})
	@GetMapping
	public ResponseEntity<List<TaskResponse>> findAll() {
		List<TaskResponse> tasks = taskService.findAll().stream()
				.map(taskMapper::toResponse)
				.toList();

		return ResponseEntity.ok(tasks);
	}

	@Operation(summary = "Find a task", description = "Finds a task by its id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Task found"),
			@ApiResponse(responseCode = "404", description = "Task not found"),
	})
	@GetMapping("/{id}")
	public ResponseEntity<TaskResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(taskMapper.toResponse(taskService.findById(id)));
	}

	@Operation(summary = "Update a task", description = "Updates a task title, description, priority and due date")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Task updated"),
			@ApiResponse(responseCode = "404", description = "Task not found")
	})
	@PutMapping("/{id}")
	public ResponseEntity<TaskResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateTaskRequest request) {
		Task updatedTask = taskService.update(id, taskMapper.toEntity(request));
		return ResponseEntity.ok(taskMapper.toResponse(updatedTask));
	}

	@Operation(summary = "Change a task's status", description = "Updates the status of a task")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Task status updated"),
			@ApiResponse(responseCode = "404", description = "Task not found")
	})
	@PatchMapping("/{id}/status")
	public ResponseEntity<TaskResponse> changeStatus(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateTaskStatusRequest request) {
		Task updatedTask = taskService.changeStatus(id, request.getStatus());
		return ResponseEntity.ok(taskMapper.toResponse(updatedTask));
	}

	@Operation(summary = "Delete a task", description = "Finds a task by its id and deletes it")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Task deleted"),
			@ApiResponse(responseCode = "404", description = "Task not found")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		taskService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
