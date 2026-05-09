// ===== NoeudOrganisationRepository.java =====
package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.NoeudOrganisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoeudOrganisationRepository extends JpaRepository<NoeudOrganisation, Long> {

    List<NoeudOrganisation> findByParentIsNullAndActifTrue();

    List<NoeudOrganisation> findByParentIdAndActifTrue(Long parentId);

    @Query("SELECT n FROM NoeudOrganisation n WHERE n.actif = true ORDER BY n.ordre ASC")
    List<NoeudOrganisation> findAllActifs();

    boolean existsByNom(String nom);
}
