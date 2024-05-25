package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentListResponse {
    private List<Assignment> assignmentList;
    private String messageStatus;
}
