package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.OgaQuestion;
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
public class OgaRequest {
    private Long ogaId;
    private Long courseId;
    private String ogaTitle;
    private String description;
    private Date dueDate;
    private String time;
    private int totalMarks;
    private List<OgaQuestion> ogaQuestions;
    private boolean visibility;
}
