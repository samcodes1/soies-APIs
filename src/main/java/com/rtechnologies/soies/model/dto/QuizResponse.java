package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Teacher;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponse {
    private Long quizId;
    private Course course;
    private Teacher teacher;
    private String quizTitle;
    private String description;
    private String file;
    private Date dueDate;
    private int totalMarks;
    private boolean visibility;
    private String messageStatus;
}
