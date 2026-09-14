package com.lukete.task_manager_api.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(UUID id) {
		super("Task with id " + id + " was not found");
	}
}
