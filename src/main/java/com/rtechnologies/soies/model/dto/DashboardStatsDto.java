package com.rtechnologies.soies.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// @JsonIgnoreProperties(ignoreUnknown = true)
public interface DashboardStatsDto {
    String getGrade();

    Float getPercent();

    Long getPopulation();
}
