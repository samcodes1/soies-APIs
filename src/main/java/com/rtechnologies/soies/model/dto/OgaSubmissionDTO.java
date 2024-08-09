package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OgaSubmissionDTO {
    private Long id;
    private Long ogaId;
    private Long courseId;
    private String studentRollNumber;
    private int totalMarks;
    private int gainedMarks;
    private double percentage;
    private String date;
    private String term;
    private String title; // Title of the OGA submission
}
