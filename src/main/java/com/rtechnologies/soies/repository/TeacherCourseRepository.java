package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    @Query("SELECT tc FROM TeacherCourse tc WHERE tc.teacherId = :teacherId")
    List<TeacherCourse> findAllByTeacherId(@Param("teacherId") Long teacherId);}
