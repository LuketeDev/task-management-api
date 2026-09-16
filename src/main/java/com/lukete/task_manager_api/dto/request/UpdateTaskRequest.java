package com.lukete.task_manager_api.dto.request;

import com.lukete.task_manager_api.entity.TaskPriority;

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

	@NotBlank
	@Size(max = 100)
	private String title;

	@Size(max = 1_000)
	private String description;

	private TaskPriority priority;

	@FutureOrPresent
	private LocalDate dueDate;
}
