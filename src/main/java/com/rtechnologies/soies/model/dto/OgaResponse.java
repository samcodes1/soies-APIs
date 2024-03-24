package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.OgaQuestion;
import com.rtechnologies.soies.model.QuizQuestion;
import com.rtechnologies.soies.model.Teacher;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OgaResponse {
    private Long ogaId;
    private Course course;
    private String ogaTitle;
    private String description;
    private List<OgaQuestion> ogaQuestions;
    private Date dueDate;
    private int totalMarks;
    private String time;
    private String term;
    private boolean visibility;
    private String messageStatus;
}
