package com.minitaskflow.tasks;

import java.time.Instant;

public record TaskResponse(Long id, Long projectId, String title, boolean completed, Instant createdAt, Instant updatedAt) {
    public static TaskResponse from(TaskItem task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.isCompleted(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
