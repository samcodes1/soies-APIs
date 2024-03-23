package com.rtechnologies.soies.model.dto;

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
public class OgaSubmissionListResponse {
    private List<OgaSubmission> ogaSubmissionList;
    private String messageStatus;
}
