package com.minitaskflow.projects;

import com.minitaskflow.users.AppUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerOrderByCreatedAtDesc(AppUser owner);

    Optional<Project> findByIdAndOwner(Long id, AppUser owner);

    long countByOwner(AppUser owner);
}
