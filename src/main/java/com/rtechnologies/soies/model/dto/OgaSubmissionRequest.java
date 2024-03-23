package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.OgaQuestion;
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
public class OgaSubmissionRequest {
    private Long ogaId;
    private Long courseId;
    private Long studentRollNumber;
    private List<OgaQuestion> ogaQuestionList;
    private int totalMarks;
}
