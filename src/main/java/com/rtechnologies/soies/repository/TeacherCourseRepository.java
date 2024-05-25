package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse,Long> {
    List<TeacherCourse> findAllByTeacherId(long teacherId);
}
