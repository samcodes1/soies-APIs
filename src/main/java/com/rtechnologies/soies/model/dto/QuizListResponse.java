package com.rtechnologies.soies.model.dto;

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
    private List<QuizResponse> quizList;
    private String messageStatus;
}
