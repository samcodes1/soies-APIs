package com.rtechnologies.soies.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rtechnologies.soies.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherListResponse {
    private List<Teacher> teacherList;
    private List<TeacherDTO> teacherCompleteList;
    List<TeacherProjection> teacherJoinData;
    Page<TeacherProjection> teacherJoinDataPage;
    private String messageStatus;
}
