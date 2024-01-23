package com.rtechnologies.soies.controller;

import com.rtechnologies.soies.model.Event;
import com.rtechnologies.soies.model.dto.EventListResponse;
import com.rtechnologies.soies.model.dto.EventResponse;
import com.rtechnologies.soies.service.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Event Management")
@RestController
@RequestMapping("/v1/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @ApiOperation(value = "Add an Event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully added the event", response = EventResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @PostMapping("/add")
    public ResponseEntity<EventResponse> addEvent(@RequestBody Event event) {
        EventResponse response = eventService.addEvent(event);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Update an Event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated the event", response = EventResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Event not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @PutMapping("/update")
    public ResponseEntity<EventResponse> updateEvent(@RequestBody Event event) {
        EventResponse response = eventService.updateEvent(event);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an Event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted the event", response = EventResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Event not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<EventResponse> deleteEvent(@PathVariable Long eventId) {
        EventResponse response = eventService.deleteEvent(eventId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Events by Teacher ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved events", response = EventListResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Teacher not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<EventListResponse> getEventsByTeacherId(@PathVariable Long teacherId) {
        EventListResponse response = eventService.getEventsByTeacherId(teacherId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Events by Course ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved events", response = EventListResponse.class),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 404, message = "Course not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<EventListResponse> getEventsByCourseId(@PathVariable Long courseId) {
        EventListResponse response = eventService.getEventsByCourseId(courseId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
