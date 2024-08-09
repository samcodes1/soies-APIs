package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmissionDTO {
    private Long submissionId;
    private Long assignmentId;
    private String studentRollNumber;
    private Long courseId;
    private String studentName;
    private String submissionDate;
    private String submittedFileURL;
    private String comments;
    private double obtainedMarks;
    private String obtainedGrade;
    private String dueDate;
    private String term;
    private int totalMarks; // Total marks for the assignment
    private String title;   // Title of the assignment
}