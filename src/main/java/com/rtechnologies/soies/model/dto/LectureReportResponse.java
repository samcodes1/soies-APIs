package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LectureReportResponse {
    private Long id;
    private String studentName;
    private String studentRollNumber;
    private long attempts;
    private String startDate;
    private String lastAccessedDate;
}
