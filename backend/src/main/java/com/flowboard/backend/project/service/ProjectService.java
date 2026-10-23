package com.flowboard.backend.project.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.flowboard.backend.project.repository.ProjectRepository;

import jakarta.transaction.Transactional;

import com.flowboard.backend.project.entity.ProjectEntity;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public ProjectEntity createProject(String name, String description) {
        ProjectEntity project = new ProjectEntity(name, description);
        return projectRepository.save(project);
    }

    public ProjectEntity updateProject(UUID id, ProjectEntity updatedData) {
        ProjectEntity project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setDescription(updatedData.getDescription());
        project.setName(updatedData.getName());
        return projectRepository.save(project);
    }

    public void save(ProjectEntity user) { projectRepository.save(user); }

    public List<ProjectEntity> findAll() { return projectRepository.findAll(); }

    public ProjectEntity findById (UUID id) { return projectRepository.findById(id).orElse(null); }

    @Transactional
    public void deleteById (UUID id) {
        projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));

        projectRepository.deleteById(id);
    }
}
