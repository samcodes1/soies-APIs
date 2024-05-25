package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findByCampusName(String campusName);

    @Query(value = "SELECT t.* from "+
    "teacher t "+
    "INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id "+
    "INNER JOIN course c ON c.course_id = tc.course_id "+
    "INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id "+
    "WHERE t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR c.course_name = ?3) "+
    "AND (?4 IS NULL OR c.grade = ?4)", nativeQuery = true)
    List<Teacher> findByCampusNameCourseGradeSection(String campusName, String section, String courseName, String grade);
}
