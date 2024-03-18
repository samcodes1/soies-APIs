package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.security.CustomUserDetails;
import com.rtechnologies.soies.repository.StudentRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentAttendanceService studentAttendanceService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to load a Teacher
        Optional<Teacher> teacher = teacherRepository.findByEmail(username);
        if (teacher.isPresent()) {
            return new CustomUserDetails(teacher.get());
        }
        // Try to load a Student
        Optional<Student> student = studentRepository.findByRollNumber(username);
        if (student.isPresent()) {
            studentAttendanceService.markAttendanceOnLogin(student.get().getRollNumber());
            return new CustomUserDetails(student.get());
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}


