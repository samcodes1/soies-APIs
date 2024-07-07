package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.TeacherProjection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    List<Teacher> findByCampusName(String campusName);

    @Query(value = "SELECT t.*,ts.`section` ,c.course_name as course, c.credits from "+
    "teacher t "+
    "INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id "+
    "INNER JOIN course c ON c.course_id = tc.course_id "+
    "INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id "+
    "WHERE t.campus_name = ?1 "+
    "AND ts.`section` = ?2 "+
    "AND c.course_name = ?3 "+
    "AND c.grade = ?4", 

    countQuery = "SELECT COUNT(*) from "+
    "teacher t "+
    "INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id "+
    "INNER JOIN course c ON c.course_id = tc.course_id "+
    "INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id "+
    "WHERE t.campus_name = ?1 "+
    "AND ts.`section` = ?2 "+
    "AND c.course_name = ?3 "+
    "AND c.grade = ?4", 

    nativeQuery = true)
    Page<TeacherProjection> findByCampusNameCourseGradeSection(String campusName, String section, String courseName, String grade, Pageable pageable);
/////////////////////////////////////////////////////////////////////////////////////////////////////

@Query(value = "SELECT t.*,ts.`section`, c.course_name as course, c.credits from  teacher t INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id INNER JOIN course c ON c.course_id = tc.course_id INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id WHERE t.campus_name = ?1 AND c.course_name = ?2", 

    countQuery = "SELECT count(*) from  teacher t INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id INNER JOIN course c ON c.course_id = tc.course_id INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id WHERE t.campus_name = ?1 AND c.course_name = ?2", 

    nativeQuery = true)
    Page<TeacherProjection> findByCampusNameCourse(String campusName, String courseName, Pageable pageable);
/////////////////////////////////////////////////////////////////////////////////////////////////////

@Query(value = "select t.teacher_id,t.address,c.campus_name,t.date_of_birth,t.email,t.employee_name,t.gender,t.joining_date,t.phone_number,s.grade,t.user_name,s.section_name as `section` from teacher t inner join teacher_campus_section_grade_branch tcsgb on t.teacher_id= tcsgb.teache_id_fk inner join `section` s on s.id=tcsgb.section_id_fk inner join campus c on c.id = s.campus_id where c.campus_name=?1 and s.grade=?2", 

    countQuery = "select count(*) from teacher t inner join teacher_campus_section_grade_branch tcsgb on t.teacher_id= tcsgb.teache_id_fk inner join `section` s on s.id=tcsgb.section_id_fk inner join campus c on c.id = s.campus_id where c.campus_name=?1 and s.grade=?2", 

    nativeQuery = true)
    Page<TeacherProjection> findByCampusNameGrade(String campusName, String grade, Pageable pageable);


////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Query(value = "SELECT * FROM teacher WHERE campus_name=?1", 

    countQuery = "SELECT COUNT(*) FROM teacher WHERE campus_name=?1", 

    nativeQuery = true)
    Page<TeacherProjection> findByCampusName(String campusName, Pageable pageable);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Query(value = "SELECT * FROM teacher WHERE campus_name=?1", 

    countQuery = "SELECT COUNT(*) FROM teacher WHERE campus_name=?1", 

    nativeQuery = true)
    List<Teacher> findByCampusNamePage(String campusName, Pageable pageable);

    //////////////////////////////////////////////////////////////

    @Query(value = "select t.teacher_id,t.address,c.campus_name,t.date_of_birth,t.email,t.employee_name,t.gender,t.joining_date,t.phone_number,s.grade,t.user_name,s.section_name as `section` from teacher t inner join teacher_campus_section_grade_branch tcsgb on t.teacher_id= tcsgb.teache_id_fk inner join `section` s on s.id=tcsgb.section_id_fk inner join campus c on c.id = s.campus_id where c.campus_name=?1 and s.section_name=?2 and s.grade=?3", 

    countQuery = "select count(*) from teacher t inner join teacher_campus_section_grade_branch tcsgb on t.teacher_id= tcsgb.teache_id_fk inner join `section` s on s.id=tcsgb.section_id_fk inner join campus c on c.id = s.campus_id where c.campus_name=?1 and s.section_name=?2 and s.grade=?3", 

    nativeQuery = true)
    Page<TeacherProjection> findByCampusNamesectionOrGrade(String campusName, String section, String grade,Pageable pageable);
    //////////////////////////////////////////////////////////////

    
    @Query(value = "SELECT t.*,ts.`section` ,c.course_name as course, c.credits from  teacher t INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id INNER JOIN course c ON c.course_id = tc.course_id INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id WHERE t.campus_name = ?1 AND ts.`section` = ?3 and c.course_name=?2", 

    countQuery = "SELECT count(*) from  teacher t INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id INNER JOIN course c ON c.course_id = tc.course_id INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id WHERE t.campus_name = ?1 AND ts.`section` = ?3 and c.course_name=?2", 

    nativeQuery = true)
    Page<TeacherProjection> findByCampusNameCourseAndSection(String campusName, String course, String section, Pageable pageable);
    //////////////////////////////////////////////////////////////

    @Query(value = "SELECT t.*,ts.`section` ,c.course_name as course, c.credits from  teacher t INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id INNER JOIN course c ON c.course_id = tc.course_id INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id WHERE t.campus_name = ?1 and c.course_name=?2 AND c.grade = ?3", 

    countQuery = "SELECT count(t.*) from  teacher t INNER JOIN teacher_course tc ON t.teacher_id = tc.teacher_id INNER JOIN course c ON c.course_id = tc.course_id INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id WHERE t.campus_name = ?1 and c.course_name=?2 AND c.grade = ?3 ", 

    nativeQuery = true)
    Page<TeacherProjection> findByCampusNameCourseAndGrade(String campusName, String course, String grade, Pageable pageable);
    //////////////////////////////////////////////////////////////

    @Query(value = "select t.teacher_id,t.address,c.campus_name,t.date_of_birth,t.email,t.employee_name,t.gender,t.joining_date,t.phone_number,s.grade,t.user_name,s.section_name as `section` from teacher t inner join teacher_campus_section_grade_branch tcsgb on t.teacher_id= tcsgb.teache_id_fk inner join `section` s on s.id=tcsgb.section_id_fk inner join campus c on c.id = s.campus_id where c.campus_name=?1 and s.section_name=?2", 

    countQuery = "select count(*) from teacher t inner join teacher_campus_section_grade_branch tcsgb on t.teacher_id= tcsgb.teache_id_fk inner join `section` s on s.id=tcsgb.section_id_fk inner join campus c on c.id = s.campus_id where c.campus_name=?1 and s.section_name=?2", 
    
    nativeQuery = true)
    Page<TeacherProjection> findByCampusNamesection(String campusName, String section, Pageable pageable);

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "SELECT t.*,ts.`section` from teacher t INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id where t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR ts.grade = ?3)", 

    countQuery = "SELECT COUNT(*) from teacher t INNER JOIN teacher_section ts ON t.teacher_id = ts.teacher_id where t.campus_name = ?1 "+
    "AND (?2 IS NULL OR ts.`section` = ?2) "+
    "AND (?3 IS NULL OR ts.grade = ?3)", 

    nativeQuery = true)
    List<TeacherProjection> findByCampusNameCourseName(String campusName, String courseName ,Pageable pageable);

}
