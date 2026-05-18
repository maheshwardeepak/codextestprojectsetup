package com.minitaskflow.dashboard;

import com.minitaskflow.projects.ProjectRepository;
import com.minitaskflow.tasks.TaskRepository;
import com.minitaskflow.users.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DashboardService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse summary(AppUser owner) {
        return new DashboardSummaryResponse(
                projectRepository.countByOwner(owner),
                taskRepository.countByOwner(owner),
                taskRepository.countByOwnerAndCompletedTrue(owner)
        );
    }
}
