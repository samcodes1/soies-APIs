package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmissionResponse {
    private Long id;
    private Long quizId;
    private Long courseId;
    private String studentRollNumber;
    private String fullName;
    private int totalMarks;
    private int gainedMarks;
    private double percentage;
    private String date;
    private String term;

}
