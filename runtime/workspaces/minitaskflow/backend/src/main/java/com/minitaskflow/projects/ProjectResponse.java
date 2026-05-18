package com.minitaskflow.projects;

import java.time.Instant;

public record ProjectResponse(Long id, String name, Instant createdAt) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getCreatedAt());
    }
}
