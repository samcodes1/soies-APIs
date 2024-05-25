package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.association.AssignmentSubmission;
import com.rtechnologies.soies.model.association.ExamSubmission;
import com.rtechnologies.soies.model.association.OgaSubmission;
import com.rtechnologies.soies.model.association.QuizSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicRecordResponse {
    private List<AssignmentSubmission> assignmentSubmissions;
    private List<ExamSubmission> examSubmissions;
    private List<QuizSubmission> quizSubmissions;
    private List<OgaSubmission> ogaSubmissions;
    private String messageStatus;
}
