package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Oga;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OgaDtoResposne {

    private List<OgaDTO> ogaList;
    private String messageStatus;

}
