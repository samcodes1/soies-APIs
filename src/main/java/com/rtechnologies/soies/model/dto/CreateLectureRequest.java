package com.rtechnologies.soies.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateLectureRequest {
    private long courseId; // ID of the Course associated with the lecture
    private String lectureTitle;
    private String description;
    private MultipartFile videoURL;
    private MultipartFile file; // or PDF URL, choose the appropriate data type
    private int totalViews;
    private boolean isVisible;
}
