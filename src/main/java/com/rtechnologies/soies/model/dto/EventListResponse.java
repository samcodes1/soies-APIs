package com.rtechnologies.soies.model.dto;

import com.rtechnologies.soies.model.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventListResponse {
    private List<Event> eventList;
    private String messageStatus;
}
