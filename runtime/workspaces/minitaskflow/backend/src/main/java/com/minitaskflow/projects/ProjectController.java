package com.minitaskflow.projects;

import com.minitaskflow.users.AppUser;
import com.minitaskflow.users.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final CurrentUserService currentUserService;
    private final ProjectService projectService;

    public ProjectController(CurrentUserService currentUserService, ProjectService projectService) {
        this.currentUserService = currentUserService;
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> listProjects() {
        AppUser user = currentUserService.currentUser();
        return projectService.list(user);
    }

    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody ProjectRequest request) {
        AppUser user = currentUserService.currentUser();
        return projectService.create(user, request);
    }
}
