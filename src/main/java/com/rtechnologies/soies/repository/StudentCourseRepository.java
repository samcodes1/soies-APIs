package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.association.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    List<StudentCourse> findAllByStudentId(Long studentId);
}
