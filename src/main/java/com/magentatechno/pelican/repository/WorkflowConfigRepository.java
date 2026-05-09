// ===== WorkflowConfigRepository.java =====
package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.Courrier;
import com.magentatechno.pelican.entity.WorkflowConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowConfigRepository extends JpaRepository<WorkflowConfig, Long> {

    List<WorkflowConfig> findByActifTrue();

    Optional<WorkflowConfig> findByDefautTrueAndActifTrue();

    Optional<WorkflowConfig> findByTypeCourrierAndActifTrue(Courrier.TypeCourrier type);

    boolean existsByNom(String nom);
}
