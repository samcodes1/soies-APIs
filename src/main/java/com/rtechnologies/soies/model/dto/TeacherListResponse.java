package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherListResponse {
    private List<Teacher> teacherList;
    private String messageStatus;
}
