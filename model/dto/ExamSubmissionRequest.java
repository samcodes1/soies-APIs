package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.ExamQuestion;
import com.rtechnologies.soies.model.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamSubmissionRequest {
    private Long examId;
    private Long courseId;
    private String studentRollNumber;
    private List<ExamQuestion> examQuestionList;
    private int totalMarks;
}
