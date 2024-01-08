package com.rtechnologies.soies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;
    private String course;
    private Long teacherId; // ID of the Teacher associated with the assignment
    private String assignmentTitle;
    private String description;
    private String file; // You might want to use a data type suitable for storing file paths
    private Date dueDate;
    private int totalMarks;
    private boolean visibility;

}

