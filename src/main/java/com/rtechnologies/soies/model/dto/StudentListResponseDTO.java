package com.rtechnologies.soies.model.dto;

import lombok.*;
import org.springframework.data.domain.Page;


@Getter
@Setter
@Builder
@Data@NoArgsConstructor
@AllArgsConstructor
public class StudentListResponseDTO {
    private Page<StudentDTO> studentPage;
    private String messageStatus;
    // Getters, Setters, Builder
}