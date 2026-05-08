package com.magentatechno.pelican.dto;

import lombok.*;

public class DashboardDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Statistics {
        private long totalCourriers;
        private long courriersEntrants;
        private long courriersSortants;
        private long courriersEnCours;
        private long courriersValidés;
        private long courriersRejetés;
        private long courriersArchivés;
        private long courriersUrgents;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyStats {
        private String date;
        private long count;
    }
}
