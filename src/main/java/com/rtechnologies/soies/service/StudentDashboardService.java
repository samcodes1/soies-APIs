package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.StudentCourse;
import com.rtechnologies.soies.model.dto.DashboardResponse;
import com.rtechnologies.soies.model.dto.StudentDashboardResponse;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.StudentCourseRepository;
import com.rtechnologies.soies.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentDashboardService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentCourseRepository studentCourseRepository;
    public StudentDashboardResponse getStudentDashboard(String rollNumber){
        StudentDashboardResponse dashboardResponse = new StudentDashboardResponse();
        getStudentName(dashboardResponse,rollNumber);
        getAttendancePer(dashboardResponse);
        getTermCompletion(dashboardResponse);

        dashboardResponse.setMessageStatus("Success");
        return dashboardResponse;
    }

    private StudentDashboardResponse getStudentName(StudentDashboardResponse dashboardResponse, String rollNumber) {
        Optional<Student> student = studentRepository.findByRollNumber(rollNumber);
        if(!student.isPresent()){
            dashboardResponse.setMessageStatus("Failure");
            throw new NotFoundException("Student not found with roll number: "+rollNumber);
        }

        dashboardResponse.setStudentName(student.get().getStudentName());
        getCourses(dashboardResponse, student.get().getStudentId());
        return dashboardResponse;
    }

    private void getAttendancePer(StudentDashboardResponse dashboardResponse){
        dashboardResponse.setAttendancePercentAge("90");
    }

    private void getTermCompletion(StudentDashboardResponse dashboardResponse){
        dashboardResponse.setTermCompletion("80");
    }

    private void getCourses(StudentDashboardResponse dashboardResponse, long studentId){
        List<StudentCourse> courseList = studentCourseRepository.findAllByStudentId(studentId);
        List<Course> courses = new ArrayList<>();
        for(StudentCourse studentCourse : courseList){
            Optional<Course> course = courseRepository.findById(studentCourse.getCourseId());
            courses.add(course.get());
        }

        dashboardResponse.setCourseList(courses);
    }
}
