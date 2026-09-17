package com.lukete.task_manager_api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.lukete.task_manager_api.entity.Task;
import com.lukete.task_manager_api.entity.TaskPriority;
import com.lukete.task_manager_api.entity.TaskStatus;

@Testcontainers
@SpringBootTest
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveAndFindTaskById() {
        Task task = createTask();
        taskRepository.save(task);
        Task found = taskRepository.findById(task.getId()).orElseThrow();

        assertTaskEquals(task, found);
    }

    @Test
    void shouldReturnEmptyWhenTaskDoesNotExistById() {
        UUID id = UUID.randomUUID();
        Optional<Task> found = taskRepository.findById(id);
        assertThat(found).isEmpty();
    }

    private Task createTask() {
        return new Task(
                "test_title",
                "test_description",
                TaskStatus.PENDING,
                TaskPriority.MEDIUM,
                LocalDate.now());
    }

    private void assertTaskEquals(Task expected, Task actual) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
        assertThat(actual.getPriority()).isEqualTo(expected.getPriority());
        assertThat(actual.getDueDate()).isEqualTo(expected.getDueDate());

    }
}