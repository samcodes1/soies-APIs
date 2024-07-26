package com.rtechnologies.soies.model.association;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}

