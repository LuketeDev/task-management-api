package com.lukete.task_manager_api.dto.request;

import com.lukete.task_manager_api.entity.TaskPriority;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

	@Schema(description = "Task title", example = "Brew coffee")
	@NotBlank
	@Size(max = 100)
	private String title;

	@Schema(description = "Task description", example = "Remember to brew coffee today!")
	@Size(max = 1_000)
	private String description;

	@Schema(description = "Task priority", example = "HIGH")
	private TaskPriority priority;

	@Schema(description = "Task due date", example = "2026-10-24")
	@FutureOrPresent
	private LocalDate dueDate;
}
