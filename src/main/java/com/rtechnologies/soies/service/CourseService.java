package com.rtechnologies.soies.service;
import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.association.TeacherCourse;
import com.rtechnologies.soies.model.dto.CourseListResponse;
import com.rtechnologies.soies.model.dto.CourseResponse;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.TeacherCourseRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

    public CourseResponse createCourse(Course course) {
        Utility.printDebugLogs("Course creation request: "+course.toString());
        CourseResponse courseResponse = null;
        try {
            if(course == null) {
                Utility.printDebugLogs("Course creation request is null: "+course.toString());
                throw new IllegalArgumentException("Corrupt data receive");
            }

            Course createdCourse = courseRepository.save(course);
            Utility.printDebugLogs("Course created successfully: "+createdCourse);
            courseResponse = CourseResponse.builder()
                                .courseId(createdCourse.getCourseId())
                    .courseName(createdCourse.getCourseName())
                    .description(createdCourse.getDescription())
                    .grade(createdCourse.getGrade())
                    .credits(createdCourse.getCredits())
                    .messageStatus("Success").build();

            Utility.printDebugLogs("Course response: "+courseResponse);
            return courseResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            courseResponse = CourseResponse.builder()
                    .messageStatus(e.toString()).build();
            return courseResponse;
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            courseResponse = CourseResponse.builder()
                    .messageStatus("Failure").build();
            return courseResponse;
        }
    }

    public CourseResponse updateCourse(Course course) {
        Utility.printDebugLogs("Course update request: " + course.toString());
        CourseResponse courseResponse = new CourseResponse();
        try {
            // Validate course
            if (course == null) {
                Utility.printErrorLogs("Course update request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            Optional<Course> optionalCourse = courseRepository.findById(course.getCourseId());
            if(!optionalCourse.isPresent()){
                Utility.printErrorLogs("No record found for Course ID: " + course.getCourseId());
                throw new NotFoundException("No record found for Course ID: " + course.getCourseId());
            }
            // Update the course
            Course updatedCourse = courseRepository.save(course);

            // Build the response object
            courseResponse = CourseResponse.builder()
                    .courseId(updatedCourse.getCourseId())
                    .courseName(updatedCourse.getCourseName())
                    .description(updatedCourse.getDescription())
                    .grade(updatedCourse.getGrade())
                    .credits(updatedCourse.getCredits())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Course updated successfully. Response: " + courseResponse);
            return courseResponse;

        } catch (NotFoundException e){
            Utility.printErrorLogs("Error updating course: " + e.getMessage());
            courseResponse.setMessageStatus(e.getMessage());
            return courseResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error updating course: " + e.getMessage());
            courseResponse.setMessageStatus(e.getMessage());
            return courseResponse;
        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error updating course: " + e.getMessage());
            courseResponse.setMessageStatus("Failure");
            return courseResponse;
        }
    }


    public CourseResponse deleteCourse(Long courseId) {
        Utility.printDebugLogs("Course deletion request for ID: " + courseId);
        CourseResponse courseResponse = new CourseResponse();

        try {
            // Validate courseId
            if (courseId == null || courseId <= 0) {
                Utility.printErrorLogs("Invalid courseId for deletion");
                throw new IllegalArgumentException("Invalid courseId for deletion");
            }

            Optional<Course> optionalCourse = courseRepository.findById(courseId);
            if(!optionalCourse.isPresent()){
                Utility.printErrorLogs("No record found for Course ID: " + courseId);
                throw new NotFoundException("No record found for Course ID: " + courseId);
            }
            // Delete the course
            courseRepository.deleteById(courseId);

            Utility.printDebugLogs("Course deleted successfully. ID: " + courseId);
            courseResponse.setMessageStatus("Success");
            return courseResponse;

        } catch (NotFoundException e){
            Utility.printErrorLogs("Error deleting course: " + e.getMessage());
            courseResponse.setMessageStatus(e.getMessage());
            return courseResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error deleting course: " + e.getMessage());
            courseResponse.setMessageStatus(e.getMessage());
            return courseResponse;
        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error deleting course: " + e.getMessage());
            courseResponse.setMessageStatus("Failure");
            return courseResponse;
        }
    }


    public CourseListResponse getAllCourses() {
        Utility.printDebugLogs("Get all courses request");
        CourseListResponse courseListResponse = new CourseListResponse();
        try {
            // Fetch all courses
            List<Course> courses = courseRepository.findAll();

            if(courses.size() <=0){
                Utility.printDebugLogs("No record found for courses");
                courseListResponse.setMessageStatus("Success");
                return courseListResponse;
            }

            Utility.printDebugLogs("Fetched courses: " + courses);

            courseListResponse.setCourseList(courses);
            courseListResponse.setMessageStatus("Success");

            Utility.printDebugLogs("Response: " + courseListResponse);
            return courseListResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error fetching all courses: " + e.getMessage());
            courseListResponse.setMessageStatus("Failure");
            return courseListResponse;
        }
    }

    public CourseListResponse getAllCoursesByGrade(String grade) {
        Utility.printDebugLogs("Get all courses by grade request: " + grade);
        CourseListResponse courseListResponse = new CourseListResponse();

        try {
            // Validate grade
            if (grade == null || grade.isEmpty()) {
                Utility.printErrorLogs("Invalid grade for fetching courses");
                courseListResponse.setMessageStatus("Failure");
                return courseListResponse;
            }

            // Fetch courses by grade
            List<Course> courses = courseRepository.findByGrade(grade);

            if(courses.size() <=0){
                Utility.printDebugLogs("No record found for courses");
                courseListResponse.setMessageStatus("Success");
                return courseListResponse;
            }

            Utility.printDebugLogs("Fetched courses: " + courses);

            courseListResponse.setCourseList(courses);
            courseListResponse.setMessageStatus("Success");

            Utility.printDebugLogs("Response " + courseListResponse);
            return courseListResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error fetching courses by grade: " + e.getMessage());
            courseListResponse.setMessageStatus("Failure");
            return courseListResponse;
        }
    }

    public CourseResponse getCourseById(Long courseId) {
        Utility.printDebugLogs("Get course by ID request: " + courseId);
        CourseResponse courseResponse = new CourseResponse();

        try {
            // Validate courseId
            if (courseId == null || courseId <= 0) {
                Utility.printErrorLogs("Invalid courseId for fetching course");
                courseResponse.setMessageStatus("Failure");
                return courseResponse;
            }

            // Fetch course by ID
            Optional<Course> optionalCourse = courseRepository.findById(courseId);

            if (optionalCourse.isPresent()) {
                Course course = optionalCourse.get();
                courseResponse = CourseResponse.builder()
                        .courseId(course.getCourseId())
                        .courseName(course.getCourseName())
                        .description(course.getDescription())
                        .grade(course.getGrade())
                        .credits(course.getCredits())
                        .messageStatus("Success")
                        .build();
                Utility.printDebugLogs("Fetched course by ID: " + courseId);
            } else {
                courseResponse.setMessageStatus("Failure");
                Utility.printDebugLogs("Course not found with ID: " + courseId);
            }

            Utility.printDebugLogs("Response: " + courseResponse);
            return courseResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error fetching course by ID: " + e.getMessage());
            courseResponse.setMessageStatus("Failure");
            return courseResponse;
        }
    }

    public CourseListResponse getCoursesByTeacherId(Long teacherId) {
        Utility.printDebugLogs("Get courses by teacher ID request: " + teacherId);
        CourseListResponse courseListResponse = new CourseListResponse();

        try {
            // Validate teacherId
            if (teacherId == null || teacherId <= 0) {
                Utility.printErrorLogs("Invalid teacherId for fetching courses");
                courseListResponse.setMessageStatus("Failure");
                return courseListResponse;
            }

            // Fetch courses by teacher ID
            List<TeacherCourse> courses = teacherCourseRepository.findAllByTeacherId(teacherId);

            List<Course> courseList = new ArrayList<>();
            for(TeacherCourse teacherCourse : courses) {
                courseList.add(courseRepository.findById(teacherCourse.getCourseId()).get());
            }

            if (!courses.isEmpty()) {
                courseListResponse = CourseListResponse.builder()
                        .courseList(courseList)
                        .messageStatus("Success")
                        .build();
                Utility.printDebugLogs("Fetched courses by teacher ID: " + teacherId);
            } else {
                courseListResponse.setMessageStatus("Failure");
                Utility.printDebugLogs("No courses found for teacher ID: " + teacherId);
            }

            Utility.printDebugLogs("Response: " + courseListResponse);
            return courseListResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error fetching courses by teacher ID: " + e.getMessage());
            courseListResponse.setMessageStatus("Failure");
            return courseListResponse;
        }
    }

}
