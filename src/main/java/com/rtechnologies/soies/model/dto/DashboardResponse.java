package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private double termCompletion;
    private double punctualityScore;
    private int totalCourses;
    private int totalGrades;
    private List<DashboardGraphStats> dashboardGraphStats;
    private String messageStatus;
}
