package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizListResponse {
    private List<Quiz> quizList;
    private String messageStatus;
}
