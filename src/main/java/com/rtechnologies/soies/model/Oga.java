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
public class Oga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ogaId;
    private Long courseId;
    private String ogaTitle;
    private String description;
    private Date dueDate;
    private String time;
    private int totalMarks;
    private boolean visibility;
    private String term;
}
