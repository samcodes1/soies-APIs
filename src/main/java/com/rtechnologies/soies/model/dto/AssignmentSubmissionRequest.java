package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmissionRequest {
    private Long submissionId;
    private Long assignmentId; // ID of the Assignment associated with the submission
    private Long studentId; // ID of the Student submitting the assignment
    private Date submissionDate;
    private MultipartFile submittedFile;
    private String comments;
    private int obtainedMarks;
}
