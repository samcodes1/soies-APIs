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
    private List<AssignmentSubmissionDTO> assignmentSubmissions;
    private List<ExamSubmissionDTO> examSubmissions;
    private List<QuizSubmissionDTO> quizSubmissions;
    private List<OgaSubmissionDTO> ogaSubmissions;
    private String messageStatus;
}
