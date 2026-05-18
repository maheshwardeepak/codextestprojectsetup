package com.minitaskflow.tasks;

import com.minitaskflow.users.AppUser;
import com.minitaskflow.users.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final CurrentUserService currentUserService;
    private final TaskService taskService;

    public TaskController(CurrentUserService currentUserService, TaskService taskService) {
        this.currentUserService = currentUserService;
        this.taskService = taskService;
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> listTasks(@PathVariable Long projectId) {
        AppUser user = currentUserService.currentUser();
        return taskService.list(user, projectId);
    }

    @PostMapping("/projects/{projectId}/tasks")
    public TaskResponse createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request) {
        AppUser user = currentUserService.currentUser();
        return taskService.create(user, projectId, request);
    }

    @PatchMapping("/tasks/{taskId}/complete")
    public TaskResponse completeTask(@PathVariable Long taskId) {
        AppUser user = currentUserService.currentUser();
        return taskService.complete(user, taskId);
    }
}
