package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTeacherId(Long teacherId);

    List<Assignment> findByCourseId(Long courseId);

    List<Assignment> findByCourseIdAndSection(Long courseId, String section);

    @Query("SELECT a FROM Assignment a JOIN Course c ON a.courseId = c.courseId WHERE c.grade = :grade AND a.courseId = :courseId")
    List<Assignment> findAssignmentsByCourseIdAndGrade(@Param("courseId") Long courseId, @Param("grade") String grade);

}
