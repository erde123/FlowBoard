package com.flowboard.backend.task.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.flowboard.backend.project.entity.ProjectEntity;
import com.flowboard.backend.project.repository.ProjectRepository;
import com.flowboard.backend.task.entity.TaskEntity;
import com.flowboard.backend.task.enums.TaskPriority;
import com.flowboard.backend.task.enums.TaskStatus;
import com.flowboard.backend.task.repository.TaskRepository;

class TaskServiceTests {
    private final TaskRepository tasks = mock(TaskRepository.class);
    private final ProjectRepository projects = mock(ProjectRepository.class);
    private final TaskService service = new TaskService(tasks, projects);
    private final UUID projectId = UUID.randomUUID();
    private final ProjectEntity project = new ProjectEntity("FlowBoard", null);

    @BeforeEach
    void setUp() {
        when(tasks.save(any(TaskEntity.class))).thenAnswer(call -> call.getArgument(0));
    }

    @Test
    void creationUsesDefaultsAndAssociatesExistingProject() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project));

        TaskEntity task = service.createTask(projectId, "Build API", "Task CRUD", null, null, null);

        assertSame(project, task.getProject());
        assertEquals("Build API", task.getTitle());
        assertEquals("Task CRUD", task.getDescription());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertEquals(TaskPriority.MEDIUM, task.getPriority());
        verify(tasks).save(task);
    }

    @Test
    void creationAcceptsExplicitEnumsAndDueDate() {
        when(projects.findById(projectId)).thenReturn(Optional.of(project));
        LocalDate due = LocalDate.of(2026, 11, 1);

        TaskEntity task = service.createTask(projectId, "Build API", null,
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH, due);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(due, task.getDueDate());
    }

    @Test
    void creationRejectsMissingProjectWithoutSaving() {
        when(projects.findById(projectId)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.createTask(projectId, "Build API", null, null, null, null));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(tasks, never()).save(any());
    }

    @Test
    void updateChangesEnumsThenPreservesThemWhenOmitted() {
        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity("Old title", "Old description", project);
        when(tasks.findById(taskId)).thenReturn(Optional.of(task));
        ProjectEntity destination = new ProjectEntity("Destination", null);
        when(projects.findById(projectId)).thenReturn(Optional.of(destination));

        service.updateTask(taskId, new TaskEntity("New title", "New description",
                TaskStatus.DONE, TaskPriority.URGENT, LocalDate.of(2026, 11, 1)), projectId);
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals(TaskPriority.URGENT, task.getPriority());
        assertEquals("New title", task.getTitle());
        assertSame(destination, task.getProject());

        service.updateTask(taskId, new TaskEntity("Final title", null, null, null, null), projectId);
        assertEquals(TaskStatus.DONE, task.getStatus());
        assertEquals(TaskPriority.URGENT, task.getPriority());
        assertNull(task.getDescription());
        assertNull(task.getDueDate());
        assertSame(destination, task.getProject());
    }

    @Test
    void updateRejectsMissingTaskWithoutSaving() {
        UUID taskId = UUID.randomUUID();
        when(tasks.findById(taskId)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateTask(taskId,
                        new TaskEntity("Title", null, TaskStatus.DONE, null, null), projectId));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(tasks, never()).save(any());
    }

    @Test
    void updateRejectsMissingDestinationBeforeChangingTask() {
        UUID taskId = UUID.randomUUID();
        TaskEntity task = new TaskEntity("Original title", null, project);
        when(tasks.findById(taskId)).thenReturn(Optional.of(task));
        when(projects.findById(projectId)).thenReturn(Optional.empty());

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.updateTask(taskId,
                        new TaskEntity("Changed title", null, TaskStatus.DONE, TaskPriority.HIGH, null), projectId));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        assertSame(project, task.getProject());
        assertEquals("Original title", task.getTitle());
        verify(tasks, never()).save(any());
    }
}
