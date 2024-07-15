package com.rtechnologies.soies.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

@Getter
@Setter
public class LectureUpdateModel {

    private long lectureId;
    private long courseId; // ID of the Course associated with the lecture
    private String lectureTitle;
    private String description;
    private MultipartFile videoURL;
    private MultipartFile powerPointURL; // or PDF URL, choose the appropriate data type
    private int totalViews;
    private boolean isVisible;
    private Date publishDate;
}
