package com.rtechnologies.soies.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.association.StudentCourse;
import com.rtechnologies.soies.model.association.TeacherCourse;
import com.rtechnologies.soies.model.dto.DashboardGraphStats;
import com.rtechnologies.soies.model.dto.DashboardResponse;
import com.rtechnologies.soies.model.dto.DashboardStatsDto;
import com.rtechnologies.soies.repository.*;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.text.DecimalFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class DashboardService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private TeacherCourseRepository teacherCourseRepository;

    @Autowired
    private CampusRepository campusRepository;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public DashboardResponse getDashboardData(String teacherEmail) {
        DashboardResponse finalResponse = new DashboardResponse();
        Optional<Teacher> teacher = teacherRepository.findByEmail(teacherEmail);

        if (!teacher.isPresent()) {
            Utility.printDebugLogs("Teacher not found with email: " + teacherEmail);
            finalResponse.setMessageStatus("Teacher not found with email");
            throw new NotFoundException("Teacher not found with email");
        }

        finalResponse = getTotalCourses(finalResponse, teacher.get().getTeacherId());
        return finalResponse;
    }

    public DashboardResponse getDashboardDataStats(String campus) throws JsonProcessingException, InterruptedException, ExecutionException {
        DashboardResponse finalResponse = new DashboardResponse();

        CompletableFuture<Long> countTeacherAsynCall = CompletableFuture.supplyAsync(() -> teacherRepository.count());
        CompletableFuture<Long> countStudentAsynCall = CompletableFuture.supplyAsync(() -> studentRepository.count());
        CompletableFuture<Long> countCampusAsynCall = CompletableFuture.supplyAsync(() -> campusRepository.count());

        CompletableFuture<List<DashboardStatsDto>> countStudentPercentageAsynCall;
        CompletableFuture<Long> countTeachersByCampus = CompletableFuture.completedFuture(0L);
        CompletableFuture<Long> countStudentsByCampus = CompletableFuture.completedFuture(0L);
        CompletableFuture<Long> countCampusesByCampus = CompletableFuture.completedFuture(0L);

        if (campus == null || campus.isEmpty()) {
            countStudentPercentageAsynCall = CompletableFuture.supplyAsync(() -> studentRepository.findPopulationPercentageInEachGrade());
        } else {
            countStudentPercentageAsynCall = CompletableFuture.supplyAsync(() -> studentRepository.findPopulationPercentageInEachGradeCampusFilter(campus));
            countTeachersByCampus = CompletableFuture.supplyAsync(() -> teacherRepository.countByCampusName(campus));
            countStudentsByCampus = CompletableFuture.supplyAsync(() -> studentRepository.countByCampusName(campus));
            countCampusesByCampus = CompletableFuture.supplyAsync(() -> campusRepository.countByCampusName(campus));
        }

        Map<String, Object> stats = new HashMap<>();

        if (campus == null || campus.isEmpty()) {
            stats.put("numberOfTeachers", countTeacherAsynCall.get());
            stats.put("numberOfStudents", countStudentAsynCall.get());
            stats.put("numberOfCampuses", countCampusAsynCall.get());
        } else {
            stats.put("numberOfTeachers", countTeachersByCampus.get());
            stats.put("numberOfStudents", countStudentsByCampus.get());
            stats.put("numberOfCampuses", countCampusesByCampus.get());
        }

        stats.put("studentsPopulationStatsInGrades", countStudentPercentageAsynCall.get());
        finalResponse.setData(stats);

        return finalResponse;
    }

    public DashboardResponse getTotalCourses(DashboardResponse dashboardResponse, long teacherId) {
        List<TeacherCourse> courses = teacherCourseRepository.findAllByTeacherId(teacherId);
        List<Course> courseList = new ArrayList<>();
        for (TeacherCourse teacherCourse : courses) {
            courseList.add(courseRepository.findById(teacherCourse.getCourseId()).get());
        }
        int courseSize = courses.size();
        dashboardResponse.setTotalCourses(courseSize);
        dashboardResponse = getTotalGrades(courseList, dashboardResponse);
        dashboardResponse = getGraphData(courseList, dashboardResponse);
        dashboardResponse.setMessageStatus("Success");
        return dashboardResponse;
    }

    public DashboardResponse getTotalGrades(List<Course> courses, DashboardResponse dashboardResponse) {
        Set<String> uniqueGrades = new HashSet<>();
        for (Course course : courses) {
            uniqueGrades.add(course.getGrade());
        }
        dashboardResponse.setTotalGrades(uniqueGrades.size());
        return dashboardResponse;
    }

    public DashboardResponse getGraphData(List<Course> courses, DashboardResponse dashboardResponse) {
        List<DashboardGraphStats> dashboardStatsList = new ArrayList<>();

        // Calculate total number of students
        int totalStudents = calculateTotalStudents(courses);

        // Calculate percentage of student population for each course and grade
        for (Course course : courses) {
            int studentCount = studentCountByCourse(course);
            double percentage = calculatePercentage(studentCount, totalStudents);

            DashboardGraphStats dashboardStats = new DashboardGraphStats();
            dashboardStats.setCourseName(course.getCourseName());
            dashboardStats.setGrade(course.getGrade());
            dashboardStats.setPercentage(percentage);

            dashboardStatsList.add(dashboardStats);
        }

        dashboardResponse.setDashboardGraphStats(dashboardStatsList);
        return dashboardResponse;
    }

    private int calculateTotalStudents(List<Course> courses) {
        int totalStudents = 0;

        for (Course course : courses) {
            totalStudents += studentCountByCourse(course);
        }

        return totalStudents;
    }

    private int studentCountByCourse(Course course) {
        List<StudentCourse> studentCourses = studentCourseRepository.findAllByCourseId(course.getCourseId());
        return studentCourses.size();
    }

    private double calculatePercentage(int count, int total) {
        if (total == 0) {
            return 0.0;
        }
        return Double.parseDouble(DECIMAL_FORMAT.format(((double) count / total) * 100));
    }

    //Get punctuality
    //Term completion

    //Dashboard code for Student Dashboard

}
