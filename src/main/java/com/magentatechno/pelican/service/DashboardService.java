package com.magentatechno.pelican.service;

import com.magentatechno.pelican.dto.DashboardDto;
import com.magentatechno.pelican.entity.Courrier;
import com.magentatechno.pelican.repository.CourrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CourrierRepository courrierRepository;

    public DashboardDto.Statistics getStatistics() {
        return DashboardDto.Statistics.builder()
                .totalCourriers(courrierRepository.count())
                .courriersEntrants(courrierRepository.countByType(Courrier.TypeCourrier.ENTRANT))
                .courriersSortants(courrierRepository.countByType(Courrier.TypeCourrier.SORTANT))
                .courriersEnCours(courrierRepository.countByStatut(Courrier.StatutCourrier.EN_COURS))
                .courriersValidés(courrierRepository.countByStatut(Courrier.StatutCourrier.VALIDE))
                .courriersRejetés(courrierRepository.countByStatut(Courrier.StatutCourrier.REJETE))
                .courriersArchivés(courrierRepository.countByStatut(Courrier.StatutCourrier.ARCHIVE))
                .build();
    }
}
