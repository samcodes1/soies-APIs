package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.association.ExamSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamSubmissionListResponse {
    private List<ExamSubmission> examSubmissionList;
    private Page<ExamSubmission> examSubmissionPage;
    private String messageStatus;
}
