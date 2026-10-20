package com.flowboard.backend.project.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowboard.backend.project.entity.ProjectEntity;
import com.flowboard.backend.project.dto.ProjectRequest;
import com.flowboard.backend.project.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    String errorText = "Project not found";

    @PostMapping
    public ResponseEntity<ProjectEntity> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectEntity project = projectService.createProject(request.name(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProject(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ProjectRequest request) {
        if (projectService.findById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorText);
        }

        ProjectEntity updatedData = new ProjectEntity(request.name(), request.description());
        return ResponseEntity.ok(projectService.updateProfile(id, updatedData));
    }

    @GetMapping
    public ResponseEntity<List<ProjectEntity>> getAllUsers() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") UUID id) {
        ProjectEntity project = projectService.findById(id);
        if (project == null) {
            return ResponseEntity.status(404).body(errorText);
        } else {
            return ResponseEntity.ok(project);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") UUID id) {
        ProjectEntity project = projectService.findById(id);
        if (project == null) {
            return ResponseEntity.status(404).body(errorText);
        } else {
            projectService.deleteById(id);
            return ResponseEntity.ok("Project deleted successfully");
        }
    }

}
