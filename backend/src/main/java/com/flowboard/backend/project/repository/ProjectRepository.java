package com.flowboard.backend.project.repository;

import java.util.UUID;

import com.flowboard.backend.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID>{

}
