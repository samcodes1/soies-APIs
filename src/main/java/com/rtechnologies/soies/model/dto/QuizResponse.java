package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.QuizQuestion;
import com.rtechnologies.soies.model.Teacher;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {
    private Long quizId;
    private Course course;
    private String quizTitle;
    private String description;
    private List<QuizQuestion> quizQuestions;
    private Date dueDate;
    private int totalMarks;
    private String time;
    private boolean visibility;
    private String messageStatus;
}
