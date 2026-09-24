package com.lukete.task_manager_api.dto.request;

import com.lukete.task_manager_api.entity.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskStatusRequest {

	@Schema(description = "New task status", example = "COMPLETED")
	@NotNull
	private TaskStatus status;
}
