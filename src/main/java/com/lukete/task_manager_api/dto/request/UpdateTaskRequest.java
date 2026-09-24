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
public class UpdateTaskRequest {

	@Schema(description = "New task title", example = "Brew coffe with chocolate")
	@NotBlank
	@Size(max = 100)
	private String title;

	@Schema(description = "New task description", example = "Remember to brew coffe today and add chocolate!")
	@Size(max = 1_000)
	private String description;

	@Schema(description = "New task priority", example = "LOW")
	private TaskPriority priority;

	@Schema(description = "New task due date", example = "2026-11-20")
	@FutureOrPresent
	private LocalDate dueDate;
}
