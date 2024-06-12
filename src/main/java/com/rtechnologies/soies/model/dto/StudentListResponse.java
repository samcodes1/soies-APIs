package com.rtechnologies.soies.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rtechnologies.soies.model.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentListResponse {
   private List<Student> studentList;
   private Page<Student> studentPage;
   private String messageStatus;
}
