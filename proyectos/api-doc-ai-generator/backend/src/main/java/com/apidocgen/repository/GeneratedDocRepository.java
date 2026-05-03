package com.apidocgen.repository;

import com.apidocgen.entity.GeneratedDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedDocRepository extends JpaRepository<GeneratedDoc, UUID> {
    List<GeneratedDoc> findByProjectIdOrderByVersionDesc(UUID projectId);
    Optional<GeneratedDoc> findFirstByProjectIdOrderByVersionDesc(UUID projectId);
}