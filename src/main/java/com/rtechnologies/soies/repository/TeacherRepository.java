package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.TeacherProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findByCampusName(String campusName);

    @Query(value = "SELECT t.*,ts.`section`,ts.grade,c.course_name as course, c.credits from "+
    "teacher t "+
    "INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id "+
    "INNER JOIN course c ON c.course_id = tc.course_id "+
    "INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id "+
    "WHERE t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR c.course_name = ?3) "+
    "AND (?4 IS NULL OR c.grade = ?4)", 

    countQuery = "SELECT COUNT(t.*) from "+
    "teacher t "+
    "INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id "+
    "INNER JOIN course c ON c.course_id = tc.course_id "+
    "INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id "+
    "WHERE t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR c.course_name = ?3) "+
    "AND (?4 IS NULL OR c.grade = ?4)", 

    nativeQuery = true)
    List<TeacherProjection> findByCampusNameCourseGradeSection(String campusName, String section, String courseName, String grade, Pageable pageable);

    @Query(value = "SELECT * FROM teacher WHERE campus_name=?1", 

    countQuery = "SELECT COUNT(*) FROM teacher WHERE campus_name=?1", 

    nativeQuery = true)
    List<TeacherProjection> findByCampusName(String campusName, Pageable pageable);

    //////////////////////////////////////////////////////////////

    @Query(value = "SELECT t.*,ts.`section`,ts.grade from teacher t INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id where t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR ts.grade = ?3)", 

    countQuery = "SELECT COUNT(t.*) from teacher t INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id where t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR ts.grade = ?3)", 

    nativeQuery = true)
    List<TeacherProjection> findByCampusNamesectionOrGrade(String campusName, String section, String grade,Pageable pageable);

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "SELECT t.*,ts.`section`,ts.grade from teacher t INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id where t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR ts.grade = ?3)", 

    countQuery = "SELECT COUNT(t.*) from teacher t INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id where t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR ts.grade = ?3)", 

    nativeQuery = true)
    List<TeacherProjection> findByCampusNameCourseName(String campusName, String courseName ,Pageable pageable);

}
