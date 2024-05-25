package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.ExamQuestion;
import com.rtechnologies.soies.model.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateExamRequest {
    private Long examId;
    private String examTitle;
    private Long courseId;
    private String description;
    private Date dueDate;
    private String time;
    private String term;
    private int totalMarks;
    private List<ExamQuestion> examQuestions;
    private boolean visibility;
}
