package com.rtechnologies.soies.service;


import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.StudentCourse;
import com.rtechnologies.soies.model.dto.CreateStudentCourse;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.StudentCourseRepository;
import com.rtechnologies.soies.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Optional;

@Service
public class StudentCourseService {
    @Autowired
    private StudentCourseRepository studentCourseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public StudentCourse createStudentCourse(StudentCourse course){
        Optional<Student> student = studentRepository.findById(course.getStudentId());

        if(student.isEmpty()){
            throw new NotFoundException("No student record found with ID: " + course.getStudentId());
        }

        Optional<Course> optionalCourse = courseRepository.findById(course.getCourseId());

        if(optionalCourse.isEmpty()){
            throw new NotFoundException("No course record found with ID: " + course.getCourseId());
        }

        return studentCourseRepository.save(course);
    }

    public StudentCourse createStudentCourse(){

        for(int i =1; i <= 22; i++){
            StudentCourse studentCourse = new StudentCourse();
            studentCourse.setCourseId(1L);
            studentCourse.setStudentId((long) i);
            studentCourseRepository.save(studentCourse);
        }
        return null;
    }
}
