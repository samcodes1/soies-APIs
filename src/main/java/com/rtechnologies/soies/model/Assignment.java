package com.rtechnologies.soies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
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
    private Long courseId;
    private Long teacherId; // ID of the Teacher associated with the assignment
    private String assignmentTitle;
    private String description;
    private String file; // You might want to use a data type suitable for storing file paths
    private String dueDate;
    private int totalMarks;
    private boolean visibility;
    private String section;
    private String term;
}

