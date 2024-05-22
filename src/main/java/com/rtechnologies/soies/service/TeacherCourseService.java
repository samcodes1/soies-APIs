package com.rtechnologies.soies.service;
import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.association.TeacherCourse;
import com.rtechnologies.soies.model.dto.TeacherCourseListResponse;
import com.rtechnologies.soies.model.dto.TeacherCourseResponse;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.TeacherCourseRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherCourseService {

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private CourseRepository courseRepository;

    public TeacherCourseResponse addTeacherCourse(TeacherCourse teacherCourse) {
        Utility.printDebugLogs("Teacher Course creation request: "+teacherCourse.toString());
        TeacherCourseResponse teacherCourseResponse = new TeacherCourseResponse();
        try {
            if(teacherCourse == null) {
                Utility.printDebugLogs("Teacher Course creation request is null: "+teacherCourse.toString());
                throw new IllegalArgumentException("Corrupt data receive");
            }

            //Check for teacher
            Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherCourse.getTeacherId());
            if(!optionalTeacher.isPresent()){
                Utility.printErrorLogs("No record found for Teacher ID: " + teacherCourse.getTeacherId());
                throw new NotFoundException("No record found for Teacher ID: " + teacherCourse.getTeacherId());
            }

            //Check for course
            Optional<Course> optionalCourse = courseRepository.findById(teacherCourse.getCourseId());
            if(!optionalCourse.isPresent()){
                Utility.printErrorLogs("No record found for Course ID: " + teacherCourse.getCourseId());
                throw new NotFoundException("No record found for Course ID: " + teacherCourse.getCourseId());
            }

            TeacherCourse savedTeacherCourse = teacherCourseRepository.save(teacherCourse);
            Utility.printDebugLogs("Teacher Course created: "+savedTeacherCourse);

            teacherCourseResponse = TeacherCourseResponse.builder()
                            .id(savedTeacherCourse.getId())
                                    .teacher(optionalTeacher.get())
                                            .course(optionalCourse.get())
                                                    .messageStatus("Success").build();

            Utility.printDebugLogs("Teacher Course creation response: "+teacherCourseResponse);
            return teacherCourseResponse;

        } catch (NotFoundException e){
            Utility.printErrorLogs("Error creating teacher course: " + e.getMessage());
            teacherCourseResponse.setMessageStatus(e.getMessage());
            return teacherCourseResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error creating teacher course: " + e.getMessage());
            teacherCourseResponse.setMessageStatus(e.getMessage());
            return teacherCourseResponse;
        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error creating teacher course: " + e.getMessage());
            teacherCourseResponse.setMessageStatus("Failure");
            return teacherCourseResponse;
        }
    }

    public TeacherCourseResponse updateTeacherCourse(TeacherCourse teacherCourse) {
        Utility.printDebugLogs("Teacher Course update request: " + teacherCourse.toString());
        TeacherCourseResponse teacherCourseResponse = new TeacherCourseResponse();

        try {
            // Validate teacherCourse
            if (teacherCourse == null) {
                Utility.printErrorLogs("Teacher Course update request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for teacher
            Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherCourse.getTeacherId());
            if (!optionalTeacher.isPresent()) {
                Utility.printErrorLogs("No record found for Teacher ID: " + teacherCourse.getTeacherId());
                throw new NotFoundException("No record found for Teacher ID: " + teacherCourse.getTeacherId());
            }

            // Check for course
            Optional<Course> optionalCourse = courseRepository.findById(teacherCourse.getCourseId());
            if (!optionalCourse.isPresent()) {
                Utility.printErrorLogs("No record found for Course ID: " + teacherCourse.getCourseId());
                throw new NotFoundException("No record found for Course ID: " + teacherCourse.getCourseId());
            }

            TeacherCourse updatedTeacherCourse = teacherCourseRepository.save(teacherCourse);
            Utility.printDebugLogs("Teacher Course updated: " + updatedTeacherCourse);

            teacherCourseResponse = TeacherCourseResponse.builder()
                    .id(updatedTeacherCourse.getId())
                    .teacher(optionalTeacher.get())
                    .course(optionalCourse.get())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Teacher Course update response: " + teacherCourseResponse);
            return teacherCourseResponse;

        } catch (NotFoundException e) {
            Utility.printErrorLogs("Error updating teacher course: " + e.getMessage());
            teacherCourseResponse.setMessageStatus(e.getMessage());
            return teacherCourseResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error updating teacher course: " + e.getMessage());
            teacherCourseResponse.setMessageStatus(e.getMessage());
            return teacherCourseResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error updating teacher course: " + e.getMessage());
            teacherCourseResponse.setMessageStatus("Failure");
            return teacherCourseResponse;
        }
    }

    public TeacherCourseResponse deleteTeacherCourse(Long teacherCourseId) {
        Utility.printDebugLogs("Teacher Course deletion request for ID: " + teacherCourseId);
        TeacherCourseResponse teacherCourseResponse = new TeacherCourseResponse();

        try {
            // Validate teacherCourseId
            if (teacherCourseId == null || teacherCourseId <= 0) {
                Utility.printErrorLogs("Invalid teacherCourseId for deletion");
                teacherCourseResponse.setMessageStatus("Failure");
                return teacherCourseResponse;
            }

            // Delete the teacher course
            teacherCourseRepository.deleteById(teacherCourseId);

            Utility.printDebugLogs("Teacher Course deleted successfully. ID: " + teacherCourseId);
            teacherCourseResponse.setMessageStatus("Success");
            return teacherCourseResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error deleting teacher course: " + e.getMessage());
            teacherCourseResponse.setMessageStatus("Failure");
            return teacherCourseResponse;
        }
    }


    public TeacherCourseListResponse getAllCoursesByTeacherId(Long teacherId) {
        Utility.printDebugLogs("Get all courses by teacher ID request: " + teacherId);
        TeacherCourseListResponse teacherCourseListResponse = new TeacherCourseListResponse();

        try {
            // Validate teacherId
            if (teacherId == null || teacherId <= 0) {
                Utility.printErrorLogs("Invalid teacherId for fetching courses");
                teacherCourseListResponse.setMessageStatus("Failure");
                return teacherCourseListResponse;
            }

            // Check for teacher
            Optional<Teacher> optionalTeacher = teacherRepository.findById(teacherId);
            if (!optionalTeacher.isPresent()) {
                Utility.printErrorLogs("No record found for Teacher ID: " + teacherId);
                throw new NotFoundException("No record found for Teacher ID: " + teacherId);
            }

            // Fetch courses by teacherId
            List<TeacherCourse> teacherCourses = teacherCourseRepository.findAllByTeacherId(teacherId);

            if(teacherCourses.size() <= 0){
                Utility.printDebugLogs("No record found for courses for teacher ID: " + teacherId);
                teacherCourseListResponse.setMessageStatus("Success");
                return teacherCourseListResponse;
            }
            Utility.printDebugLogs("Fetched:" + teacherCourses + " courses by teacher ID: " + teacherId);

            teacherCourseListResponse.setTeacherCourses(teacherCourses);
            teacherCourseListResponse.setMessageStatus("Success");

            Utility.printDebugLogs("Response: " + teacherCourseListResponse);

            return teacherCourseListResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error fetching courses by teacher ID: " + e.getMessage());
            teacherCourseListResponse.setMessageStatus("Failure");
            return teacherCourseListResponse;
        }
    }
}

