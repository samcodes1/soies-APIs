package com.rtechnologies.soies.model.dto;


import com.rtechnologies.soies.model.association.AssignmentSubmission;
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
public class OgaSubmissionsResponse {
    private List<OgaSubmission> ogaSubmissionList;
    private List<AssignmentSubmission> assignmentSubmissionList;  // Added for assignment submissions
    private List<QuizSubmission> quizSubmissionList;
    private String messageStatus;
    private int currentPage;    // Current page number
    private int totalPages;     // Total number of pages
}

