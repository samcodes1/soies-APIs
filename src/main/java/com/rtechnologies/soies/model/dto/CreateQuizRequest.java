package com.rtechnologies.soies.model.dto;

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
public class CreateQuizRequest {
    private Long courseId;
    private String quizTitle;
    private String description;
    private Date dueDate;
    private String time;
    private int totalMarks;
    private List<QuizQuestion> quizQuestions;
    private boolean visibility;
}
