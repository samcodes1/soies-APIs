package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LectureResponse {
    private long lectureId;
    private long courseId; // ID of the Course associated with the lecture
    private String lectureTitle;
    private String description;
    private String videoURL;
    private String powerPointURL; // or PDF URL, choose the appropriate data type
    private int totalViews;
    private boolean isVisible;
    private String messageStatus;
}
