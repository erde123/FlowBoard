package com.flowboard.backend.task.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.flowboard.backend.task.enums.TaskPriority;
import com.flowboard.backend.task.enums.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(
    @NotBlank @Size(max = 150) String title,
    @Size(max = 2000) String description,
    TaskStatus status,
    TaskPriority priority,
    @NotNull UUID projectId,
    LocalDate dueDate
) {
}