package com.incident.assistant.repository;

import com.incident.assistant.model.IncidentThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentThreadRepository extends JpaRepository<IncidentThread, String> {
    List<IncidentThread> findAllByOrderByCreatedAtDesc();
    List<IncidentThread> findByStatusOrderByCreatedAtDesc(IncidentThread.ThreadStatus status);
}
