package com.rtechnologies.soies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long lectureId;
    private long courseId; // ID of the Course associated with the lecture
    private String lectureTitle;
    private String description;
    private String videoURL;
    private String powerPointURL; // or PDF URL, choose the appropriate data type
    private int totalViews;
    private boolean isVisible;
    private Date publishDate;
}

