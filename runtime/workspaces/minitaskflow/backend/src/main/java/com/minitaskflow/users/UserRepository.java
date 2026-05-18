package com.minitaskflow.users;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);

    Optional<AppUser> findByGoogleSub(String googleSub);

    boolean existsByEmailIgnoreCase(String email);
}
