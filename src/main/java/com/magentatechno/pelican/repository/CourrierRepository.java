package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.Courrier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

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
    long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);

    Page<Courrier> findByCreateurId(Long createurId, Pageable pageable);

    Page<Courrier> findByAssigneAId(Long assigneId, Pageable pageable);

    @Query("SELECT c FROM Courrier c WHERE " +
            "(:search IS NULL OR LOWER(c.objet) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.numero) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(c.expediteur) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:type IS NULL OR c.type = :type) " +
            "AND (:statut IS NULL OR c.statut = :statut)")
    Page<Courrier> searchAdvanced(
            @Param("search") String search,
            @Param("type") Courrier.TypeCourrier type,
            @Param("statut") Courrier.StatutCourrier statut,
            Pageable pageable
    );

    @Query("SELECT c FROM Courrier c " +
           "LEFT JOIN FETCH c.historiques h " +
           "LEFT JOIN FETCH h.user " +
           "LEFT JOIN FETCH c.etapeCourante e " +
           "LEFT JOIN FETCH c.workflow " +
           "LEFT JOIN FETCH c.createur " +
           "LEFT JOIN FETCH c.assigneA " +
           "WHERE c.id = :id")
    Optional<Courrier> findByIdForPdf(@Param("id") Long id);
}
