package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Event;
import com.rtechnologies.soies.model.Exam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamListResponse {
    private List<Exam> examList;
    private String messageStatus;
}
