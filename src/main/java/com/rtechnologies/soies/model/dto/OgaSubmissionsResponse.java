package com.rtechnologies.soies.model.dto;


import com.rtechnologies.soies.model.association.OgaSubmission;
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
    private String messageStatus;
    private int currentPage;    // Current page number
    private int totalPages;     // Total number of pages
}

