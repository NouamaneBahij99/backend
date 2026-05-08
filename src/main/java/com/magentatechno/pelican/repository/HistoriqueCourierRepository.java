package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.HistoriqueCourrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueCourierRepository extends JpaRepository<HistoriqueCourrier, Long> {

    @Query("SELECT h FROM HistoriqueCourrier h WHERE h.courrier.id = :courrierId ORDER BY h.date DESC")
    List<HistoriqueCourrier> findByCourrierId(@Param("courrierId") Long courrierId);
}
