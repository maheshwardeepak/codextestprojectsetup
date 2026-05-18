package com.minitaskflow.projects;

import com.minitaskflow.users.AppUser;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> list(AppUser owner) {
        return projectRepository.findByOwnerOrderByCreatedAtDesc(owner).stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional
    public ProjectResponse create(AppUser owner, ProjectRequest request) {
        Project project = projectRepository.save(new Project(owner, request.name().trim()));
        return ProjectResponse.from(project);
    }

    @Transactional(readOnly = true)
    public Project getOwnedProject(AppUser owner, Long projectId) {
        return projectRepository.findByIdAndOwner(projectId, owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project was not found."));
    }
}
