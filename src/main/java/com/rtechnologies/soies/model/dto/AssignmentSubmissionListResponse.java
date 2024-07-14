package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.association.AssignmentSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmissionListResponse {
    private List<AssignmentSubmission> assignmentSubmissionResponseList;
    private Page<AssignmentSubmission> assignmentSubmissionResponsePage;
    private String messageStatus;
}
