package com.incident.assistant.repository;

import com.incident.assistant.model.Resolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResolutionRepository extends JpaRepository<Resolution, String> {
    Optional<Resolution> findByThreadId(String threadId);
}
