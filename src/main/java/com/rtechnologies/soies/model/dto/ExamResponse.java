package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Exam;
import com.rtechnologies.soies.model.ExamQuestion;
import com.rtechnologies.soies.model.QuizQuestion;
import com.rtechnologies.soies.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponse {
    private Long examId;
    private Course course;
    private String examTitle;
    private String description;
    private List<ExamQuestion> examQuestions;
    private Date dueDate;
    private int totalMarks;
    private String time;
    private boolean visibility;
    private String messageStatus;
    private Page<Exam> examListingPage;
}
