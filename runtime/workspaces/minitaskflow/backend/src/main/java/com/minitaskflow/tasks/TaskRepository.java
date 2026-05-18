package com.minitaskflow.tasks;

import com.minitaskflow.projects.Project;
import com.minitaskflow.users.AppUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByProjectAndOwnerOrderByCreatedAtDesc(Project project, AppUser owner);

    Optional<TaskItem> findByIdAndOwner(Long id, AppUser owner);

    long countByOwner(AppUser owner);

    long countByOwnerAndCompletedTrue(AppUser owner);
}
