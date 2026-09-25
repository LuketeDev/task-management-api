package com.lukete.task_manager_api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.lukete.task_manager_api.dto.request.CreateTaskRequest;
import com.lukete.task_manager_api.dto.request.UpdateTaskRequest;
import com.lukete.task_manager_api.dto.response.TaskResponse;
import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.entity.TaskPriority;
import com.lukete.task_manager_api.entity.TaskStatus;

class TaskMapperTest {
    private final TaskMapper taskMapper = new TaskMapper();

    @Test
    void shouldMapCreateTaskRequestToTask() {
        CreateTaskRequest request = new CreateTaskRequest(
                "Write tests",
                "Controller tests",
                TaskPriority.HIGH,
                LocalDate.of(2026, 9, 30));

        Task task = taskMapper.toEntity(request);

        assertThat(task.getTitle()).isEqualTo("Write tests");
        assertThat(task.getDescription()).isEqualTo("Controller tests");
        assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2026, 9, 30));
        assertThat(task.getStatus()).isNull();
    }

    @Test
    void shouldMapUpdateTaskRequestToTask() {
        UpdateTaskRequest request = new UpdateTaskRequest(
                "Write new tests",
                "Service tests",
                TaskPriority.MEDIUM,
                LocalDate.of(2026, 10, 25));

        Task task = taskMapper.toEntity(request);

        assertThat(task.getTitle()).isEqualTo("Write new tests");
        assertThat(task.getDescription()).isEqualTo("Service tests");
        assertThat(task.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(task.getDueDate()).isEqualTo(LocalDate.of(2026, 10, 25));
        assertThat(task.getStatus()).isNull();
    }

    @Test
    void shouldMapTaskToResponse() {
        Task task = new Task(
                "Write some tests",
                "Integration tests",
                TaskStatus.IN_PROGRESS,
                TaskPriority.LOW,
                LocalDate.of(2026, 11, 5));

        TaskResponse response = taskMapper.toResponse(task);

        assertThat(response.getId()).isEqualTo(task.getId());
        assertThat(response.getTitle()).isEqualTo("Write some tests");
        assertThat(response.getDescription()).isEqualTo("Integration tests");
        assertThat(response.getPriority()).isEqualTo(TaskPriority.LOW);
        assertThat(response.getDueDate()).isEqualTo(LocalDate.of(2026, 11, 5));
        assertThat(response.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response.getCreatedAt()).isEqualTo(task.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(task.getUpdatedAt());
    }
}
