package org.example.jtsb01.user.repository;

import java.util.Optional;
import org.example.jtsb01.user.entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByUsername(String username);
}