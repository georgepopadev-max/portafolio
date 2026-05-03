package com.apidocgen.repository;

import com.apidocgen.entity.SourceUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SourceUploadRepository extends JpaRepository<SourceUpload, UUID> {
    List<SourceUpload> findByProjectId(UUID projectId);
}