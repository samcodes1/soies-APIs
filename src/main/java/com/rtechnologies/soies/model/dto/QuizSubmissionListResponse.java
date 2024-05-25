package com.rtechnologies.soies.model.dto;

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
public class QuizSubmissionListResponse {
    private List<QuizSubmissionResponse> quizSubmissionList;
    private String messageStatus;
}
