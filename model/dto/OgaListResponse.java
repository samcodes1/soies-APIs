package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Oga;
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
public class OgaListResponse {
    private List<Oga> ogaList;
    private String messageStatus;
}
