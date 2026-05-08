package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.Courrier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CourrierRepository extends JpaRepository<Courrier, Long> {

    Page<Courrier> findByType(Courrier.TypeCourrier type, Pageable pageable);

    Page<Courrier> findByStatut(Courrier.StatutCourrier statut, Pageable pageable);

    Page<Courrier> findByArchive(boolean archive, Pageable pageable);

    @Query("SELECT c FROM Courrier c WHERE " +
           "LOWER(c.objet) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.numero) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.expediteur) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.destinataire) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Courrier> search(@Param("search") String search, Pageable pageable);

    long countByStatut(Courrier.StatutCourrier statut);

    long countByType(Courrier.TypeCourrier type);

    @Query("SELECT COUNT(c) FROM Courrier c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Page<Courrier> findByCreateurId(Long createurId, Pageable pageable);

    Page<Courrier> findByAssigneAId(Long assigneId, Pageable pageable);
}
