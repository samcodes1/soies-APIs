package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamDTO {
    private Long examId;
    private Long courseId;
    private String examTitle;
    private String description;
    private String term;
    private Date dueDate;
    private String time;
    private int totalMarks;
    private boolean visibility;
    private boolean hasAttempted;  // New field to indicate if the student has attempted the exam
}
