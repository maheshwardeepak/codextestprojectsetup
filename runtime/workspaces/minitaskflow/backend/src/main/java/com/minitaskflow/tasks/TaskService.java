package com.minitaskflow.tasks;

import com.minitaskflow.projects.Project;
import com.minitaskflow.projects.ProjectService;
import com.minitaskflow.users.AppUser;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {
    private final ProjectService projectService;
    private final TaskRepository taskRepository;

    public TaskService(ProjectService projectService, TaskRepository taskRepository) {
        this.projectService = projectService;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> list(AppUser owner, Long projectId) {
        Project project = projectService.getOwnedProject(owner, projectId);
        return taskRepository.findByProjectAndOwnerOrderByCreatedAtDesc(project, owner).stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Transactional
    public TaskResponse create(AppUser owner, Long projectId, TaskRequest request) {
        Project project = projectService.getOwnedProject(owner, projectId);
        TaskItem task = taskRepository.save(new TaskItem(project, owner, request.title().trim()));
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse complete(AppUser owner, Long taskId) {
        TaskItem task = taskRepository.findByIdAndOwner(taskId, owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found."));
        task.markCompleted();
        return TaskResponse.from(task);
    }
}
