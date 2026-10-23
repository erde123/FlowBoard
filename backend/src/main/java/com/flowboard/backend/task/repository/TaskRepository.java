package com.flowboard.backend.task.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.flowboard.backend.task.entity.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity,UUID>{


}
