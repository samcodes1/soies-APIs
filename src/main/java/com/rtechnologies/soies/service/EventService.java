package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Event;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.EventListResponse;
import com.rtechnologies.soies.model.dto.EventResponse;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.EventRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    public EventResponse addEvent(Event event) {
        Utility.printDebugLogs("Event creation request: " + event.toString());
        EventResponse eventResponse;

        try {
            if (event == null) {
                Utility.printDebugLogs("Event creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(event.getTeacherId());
            if (teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + event.getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + event.getTeacherId());
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(event.getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + event.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + event.getCourseId());
            }

            Event createdEvent = eventRepository.save(event);
            Utility.printDebugLogs("Event created successfully: " + createdEvent);

            eventResponse = EventResponse.builder()
                    .id(createdEvent.getId())
                    .course(course.get())
                    .teacher(teacher.get())
                    .title(createdEvent.getTitle())
                    .eventDate(createdEvent.getEventDate())
                    .type(createdEvent.getType())
                    .description(createdEvent.getDescription())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Event response: " + eventResponse);
            return eventResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return EventResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return EventResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public EventResponse updateEvent(Event event) {
        Utility.printDebugLogs("Event update request: " + event.toString());
        EventResponse eventResponse;

        try {
            if (event == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for event
            Optional<Event> existingEventOptional = eventRepository.findById(event.getId());
            if (existingEventOptional.isEmpty()) {
                throw new IllegalArgumentException("No Event found with ID: " + event.getId());
            }

            // Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(event.getTeacherId());
            if (teacher.isEmpty()) {
                throw new IllegalArgumentException("No teacher found with ID: " + event.getTeacherId());
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(event.getCourseId());
            if (course.isEmpty()) {
                throw new IllegalArgumentException("No course found with ID: " + event.getCourseId());
            }

            Event updatedEvent = eventRepository.save(event);
            Utility.printDebugLogs("Event updated successfully: " + updatedEvent);

            eventResponse = EventResponse.builder()
                    .id(updatedEvent.getId())
                    .course(course.get())
                    .teacher(teacher.get())
                    .title(updatedEvent.getTitle())
                    .eventDate(updatedEvent.getEventDate())
                    .type(updatedEvent.getType())
                    .description(updatedEvent.getDescription())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Event response: " + eventResponse);
            return eventResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return EventResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return EventResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public EventResponse deleteEvent(Long eventId) {
        Utility.printDebugLogs("Event deletion request: " + eventId);
        EventResponse eventResponse;

        try {
            Optional<Event> existingEvent = eventRepository.findById(eventId);

            if (existingEvent.isEmpty()) {
                throw new IllegalArgumentException("No event found with ID: " + eventId);
            }

            eventRepository.deleteById(eventId);
            Utility.printDebugLogs("Event deleted successfully: " + existingEvent.get());

            eventResponse = EventResponse.builder()
                    .id(existingEvent.get().getId())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Event response: " + eventResponse);
            return eventResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return EventResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return EventResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public EventListResponse getEventsByTeacherId(Long teacherId) {
        Utility.printDebugLogs("Get all events by teacher ID: " + teacherId);
        EventListResponse eventListResponse;

        try {
            Optional<Teacher> teacher = teacherRepository.findById(teacherId);

            if (teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + teacherId);
                throw new IllegalArgumentException("No teacher found with ID: " + teacherId);
            }

            List<Event> events = eventRepository.findByTeacherId(teacherId);

            eventListResponse = EventListResponse.builder()
                    .eventList(events)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Event list response: " + eventListResponse);
            return eventListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return EventListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return EventListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public EventListResponse getEventsByCourseId(Long courseId) {
        Utility.printDebugLogs("Get events by course ID: " + courseId);
        EventListResponse eventListResponse;

        try {
            List<Event> eventList = eventRepository.findByCourseId(courseId);

            if (eventList.isEmpty()) {
                Utility.printDebugLogs("No events found for course ID: " + courseId);
                throw new IllegalArgumentException("No events found for course ID: " + courseId);
            }

            eventListResponse = EventListResponse.builder()
                    .eventList(eventList)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Event list response: " + eventListResponse);
            return eventListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return EventListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return EventListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

}
