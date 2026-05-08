package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.HistoriqueCourrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoriqueCourrierRepository
        extends JpaRepository<HistoriqueCourrier, Long> {

    @Query("SELECT h FROM HistoriqueCourrier h " +
           "LEFT JOIN FETCH h.user " +
           "WHERE h.courrier.id = :courrierId " +
           "ORDER BY h.date DESC")
    List<HistoriqueCourrier> findByCourrierIdOrderByDateDesc(
            @Param("courrierId") Long courrierId
    );
}
