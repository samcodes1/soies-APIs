package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OgaDTO {
    private Long ogaId;
    private Long courseId;
    private String ogaTitle;
    private String description;
    private Date dueDate;
    private String term;
    private boolean visibility;
    private boolean hasAttempted;  // Indicates if the student has attempted this OGA
}

