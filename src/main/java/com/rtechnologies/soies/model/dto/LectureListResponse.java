package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LectureListResponse {
    private List<Lecture> lectureList;
    private String messageStatus;
}
