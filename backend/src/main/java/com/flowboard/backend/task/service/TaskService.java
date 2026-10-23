package com.flowboard.backend.task.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flowboard.backend.project.entity.ProjectEntity;
import com.flowboard.backend.project.repository.ProjectRepository;
import com.flowboard.backend.task.entity.TaskEntity;
import com.flowboard.backend.task.enums.TaskPriority;
import com.flowboard.backend.task.enums.TaskStatus;
import com.flowboard.backend.task.repository.TaskRepository;

import jakarta.transaction.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public TaskEntity createTask(UUID projectId, String title, String description,
            TaskStatus status, TaskPriority priority, LocalDate dueDate) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        TaskEntity task = new TaskEntity(title, description, project);
        if (status != null) {
            task.setStatus(status);
        }
        if (priority != null) {
            task.setPriority(priority);
        }
        task.setDueDate(dueDate);
        return taskRepository.save(task);
    }

    @Transactional
    public TaskEntity updateTask(UUID id, TaskEntity updatedData, UUID projectId) {
        TaskEntity task = requireTask(id);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        task.setTitle(updatedData.getTitle());
        task.setDescription(updatedData.getDescription());
        if (updatedData.getStatus() != null) {
            task.setStatus(updatedData.getStatus());
        }
        if (updatedData.getPriority() != null) {
            task.setPriority(updatedData.getPriority());
        }
        task.setDueDate(updatedData.getDueDate());
        task.setProject(project);
        return taskRepository.save(task);
    }

    public List<TaskEntity> findAll() {
        return taskRepository.findAll();
    }

    public TaskEntity findById(UUID id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteById(UUID id) {
        TaskEntity task = requireTask(id);
        taskRepository.delete(task);
    }

    private TaskEntity requireTask(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }
}
