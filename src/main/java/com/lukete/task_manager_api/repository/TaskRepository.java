package com.lukete.task_manager_api.repository;

import com.lukete.task_manager_api.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
