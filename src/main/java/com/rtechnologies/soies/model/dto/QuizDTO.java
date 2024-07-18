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


public class QuizDTO {


    private Long quizId;
    private Long courseId;
    private String quizTitle;
    private String description;
    private List<QuizQuestion> quizQuestions;
    private Date dueDate;
    private int totalMarks;
    private String time;
    private boolean visibility;
    private String term;
}
