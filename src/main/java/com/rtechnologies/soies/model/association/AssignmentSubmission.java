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
    private Long assignmentId; // ID of the Assignment associated with the submission
    private Long studentId; // ID of the Student submitting the assignment
    private Date submissionDate;
    private String submittedFileURL; // You might want to use a data type suitable for storing file URLs
    private String comments;
    private int obtainedMarks;

}

