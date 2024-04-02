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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentAttendanceService studentAttendanceService;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try to load a Teacher
        Optional<Teacher> teacher = teacherRepository.findByEmail(username);
        if (teacher.isPresent()){
           return CustomUserDetails.builder()
                    .username(teacher.get().getEmail())
                    .password(teacher.get().getPassword())
                    .roles(Set.of("ROLE_TEACHER"))
                   .teacher(teacher.get())
                   .student(null)
                    .authorities(Collections.singletonList(() -> "ROLE_TEACHER")) // Assuming "ROLE_TEACHER" as the authority for teachers
                    .build();
        }

        // Try to load a Student
        Optional<Student> student = studentRepository.findByRollNumber(username);
        if (student.isPresent()) {
            studentAttendanceService.markAttendanceOnLogin(student.get().getRollNumber());
            // Create UserDetails object for student
            return CustomUserDetails.builder()
                    .username(student.get().getRollNumber())
                    .password(student.get().getPassword())
                    .roles(Set.of("ROLE_STUDENT"))
                    .teacher(null)
                    .student(student.get())
                    .authorities(Collections.singletonList(() -> "ROLE_STUDENT")) // Assuming "ROLE_STUDENT" as the authority for students
                    .build();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }

}


