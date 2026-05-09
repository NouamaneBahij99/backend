// ===== EtapeCourrierRepository.java =====
package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.EtapeCourrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtapeCourrierRepository extends JpaRepository<EtapeCourrier, Long> {

    List<EtapeCourrier> findByCourrierIdOrderByEtapeOrdreAsc(Long courrierId);

    @Query("SELECT ec FROM EtapeCourrier ec WHERE ec.courrier.id = :courrierId AND ec.statut = 'EN_COURS'")
    Optional<EtapeCourrier> findEtapeCouranteByCourrierID(@Param("courrierId") Long courrierId);

    @Query("SELECT ec FROM EtapeCourrier ec WHERE ec.responsable.id = :userId AND ec.statut = 'EN_COURS'")
    List<EtapeCourrier> findEtapesEnCoursByResponsable(@Param("userId") Long userId);
}
