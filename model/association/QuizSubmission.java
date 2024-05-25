package com.rtechnologies.soies.model.association;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quizId;
    private Long courseId;
    private String studentRollNumber;
    private int totalMarks;
    private int gainedMarks;
    private double percentage;
    private String date;
    private String term;
}
