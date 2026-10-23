package com.flowboard.backend.task.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowboard.backend.project.service.ProjectService;
import com.flowboard.backend.task.dto.TaskRequest;
import com.flowboard.backend.task.entity.TaskEntity;
import com.flowboard.backend.task.service.TaskService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping ("api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ProjectService projectService;

    String errorText = "Task not found";

    TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<TaskEntity> createTask(@Valid @RequestBody TaskRequest request) {
        TaskEntity task = taskService.createTask(
            request.projectId(),
            request.title(),
            request.description(),
            request.status(),
            request.priority(),
            request.dueDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateTask(
            @PathVariable ("id") UUID id,
            @Valid @RequestBody TaskRequest request) {
        if (taskService.findById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorText);
        }
        if (projectService.findById(request.projectId()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
        }

        TaskEntity updatedData = new TaskEntity(
            request.title(),
            request.description(),
            request.status(),
            request.priority(),
            request.dueDate()
        );
        return ResponseEntity.ok(taskService.updateTask(id, updatedData, request.projectId()));
    }

    @GetMapping
    public ResponseEntity<List<TaskEntity>> getAllProjects() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable("id") UUID id) {
        TaskEntity project = taskService.findById(id);
        if (project == null) {
            return ResponseEntity.status(404).body(errorText);
        } else {
            return ResponseEntity.ok(project);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable("id") UUID id) {
        TaskEntity project = taskService.findById(id);
        if (project == null) {
            return ResponseEntity.status(404).body(errorText);
        } else {
            taskService.deleteById(id);
            return ResponseEntity.ok("Task deleted successfully");
        }
    }

}
