package com.lukete.task_manager_api.controller;

import com.lukete.task_manager_api.dto.request.CreateTaskRequest;
import com.lukete.task_manager_api.dto.request.UpdateTaskRequest;
import com.lukete.task_manager_api.dto.request.UpdateTaskStatusRequest;
import com.lukete.task_manager_api.dto.response.TaskResponse;
import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.mapper.TaskMapper;
import com.lukete.task_manager_api.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;
	private final TaskMapper taskMapper;

	@PostMapping
	/*
	 * test
	 */
	public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
		Task createdTask = taskService.create(taskMapper.toEntity(request));
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(createdTask.getId())
				.toUri();

		return ResponseEntity.created(location).body(taskMapper.toResponse(createdTask));
	}

	@GetMapping
	public ResponseEntity<List<TaskResponse>> findAll() {
		List<TaskResponse> tasks = taskService.findAll().stream()
				.map(taskMapper::toResponse)
				.toList();

		return ResponseEntity.ok(tasks);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TaskResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(taskMapper.toResponse(taskService.findById(id)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TaskResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateTaskRequest request) {
		Task updatedTask = taskService.update(id, taskMapper.toEntity(request));
		return ResponseEntity.ok(taskMapper.toResponse(updatedTask));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<TaskResponse> changeStatus(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateTaskStatusRequest request) {
		Task updatedTask = taskService.changeStatus(id, request.getStatus());
		return ResponseEntity.ok(taskMapper.toResponse(updatedTask));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		taskService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
