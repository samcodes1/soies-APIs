package com.rtechnologies.soies.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface DashboardStatsDto {
    Float getPercent();
    String getGrade();
}
