package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.dto.DashboardStatsDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// example change
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNum);
    Long countByCampusName(String campusName);


    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.studentAttendance WHERE s.rollNumber = :rollNumber")
    Optional<Student> findByRollnumber(@Param("rollNumber") String rollNumber);

    void deleteByRollNumber(String rollNum);

    Page<Student> findAllByCampusName(String campusName, Pageable pageable);

    @Query(value = "SELECT s.* FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
            "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
            "s.campus_name = ?1 " +
            "AND (?2 IS NULL OR s.grade = ?2) " +
            "AND (?3 IS NULL OR s.section_name = ?3) " +
            "AND (?4 IS NULL OR c.course_name = ?4)",
            countQuery = "SELECT COUNT(*) FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
                    "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
                    "s.campus_name = ?1 " +
                    "AND (?2 IS NULL OR s.grade = ?2) " +
                    "AND (?3 IS NULL OR s.section_name = ?3) " +
                    "AND (?4 IS NULL OR c.course_name = ?4)",
            nativeQuery = true)
    Page<Student> findByGradeAndSectionNameAndStudentCourses(String campusName, String grade, String sectionName, String course, Pageable page);

    @Query(value = "SELECT * FROM student WHERE campus_name=?1",
            countQuery = "SELECT COUNT(*) FROM student WHERE campus_name=?1",
            nativeQuery = true)
    Page<Student> findbycampus(String campusName, Pageable pageable);

    @Query(value = "SELECT s.* FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
            "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
            "s.campus_name = ?1 and c.course_name=?2",
            countQuery = "SELECT COUNT(*) FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
                    "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
                    "s.campus_name = ?1 and c.course_name=?2",
            nativeQuery = true)
    Page<Student> findbycampusAndCourse(String campusName, String course, Pageable page);

    @Query(value = "SELECT s.* FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
            "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
            "s.campus_name = ?1 and c.course_name=?2 and s.grade=?3",
            countQuery = "SELECT COUNT(*) FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
                    "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
                    "s.campus_name = ?1 and c.course_name=?2 and s.grade=?3",
            nativeQuery = true)
    Page<Student> findbycampusAndCourseAndGrade(String campusName, String course, String grade, Pageable page);

    @Query(value = "SELECT s.* FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
            "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
            "s.campus_name = ?1 and c.course_name=?2 and s.grade=?3 and s.section_name=?4",
            countQuery = "SELECT COUNT(s.*) FROM student s INNER JOIN student_course sc ON s.student_id = sc.student_id " +
                    "INNER JOIN course c ON sc.course_id = c.course_id WHERE " +
                    "s.campus_name = ?1 and c.course_name=?2 and s.grade=?3 and s.section_name=?4",
            nativeQuery = true)
    Page<Student> findbycampusAndCourseAndGradeAndSection(String campusName, String course, String grade, String section, Pageable page);

    @Query(value = "SELECT s.* FROM student s WHERE " +
            "s.campus_name = ?1 and s.grade=?2 and s.section_name=?3",
            countQuery = "SELECT COUNT(*) FROM student s WHERE " +
                    "s.campus_name = ?1 and s.grade=?2 and s.section_name=?3",
            nativeQuery = true)
    Page<Student> findbycampusAndGradeAndSection(String campusName, String grade, String section, Pageable page);

    @Query(value = "SELECT s.* FROM student s WHERE " +
            "s.campus_name = ?1 and s.section_name=?2",
            countQuery = "SELECT COUNT(*) FROM student s WHERE " +
                    "s.campus_name = ?1 and s.section_name=?2",
            nativeQuery = true)
    Page<Student> findbycampusAndSection(String campusName, String section, Pageable page);

    @Query(value = "SELECT s.* FROM student s WHERE " +
            "s.campus_name = ?1 and s.grade=?2",
            countQuery = "SELECT COUNT(*) FROM student s WHERE " +
                    "s.campus_name = ?1 and s.grade=?2",
            nativeQuery = true)
    Page<Student> findbycampusAndGrade(String campusName, String grade, Pageable page);


    @Query(value = "SELECT s.grade, (COUNT(s.student_id) * 100 / (SELECT COUNT(student_id) FROM student)) as percent, COUNT(s.student_id) as population FROM student s GROUP BY s.grade", nativeQuery = true)
    List<DashboardStatsDto> findPopulationPercentageInEachGrade();


    @Query(value = "SELECT s.grade, (COUNT(s.student_id) * 100 / (SELECT COUNT(student_id) FROM student where campus_name = :campusFilter)) as percent, COUNT(s.student_id) as population FROM student s where campus_name = :campusFilter GROUP BY s.grade", nativeQuery = true)
    List<DashboardStatsDto> findPopulationPercentageInEachGradeCampusFilter(@Param("campusFilter") String campus);


    @Query(value = "SELECT grade, COUNT(student_id) as students_in_grade FROM student GROUP BY grade", nativeQuery = true)
    List<DashboardStatsDto> findPopulationInEachGrade();

    @Query(value = "SELECT s FROM Student s WHERE s.campusName = :campusName",
            countQuery = "SELECT count(s) FROM Student s WHERE s.campusName = :campusName")
    Page<Student> findbycampusStudent(@Param("campusName") String campusName, Pageable pageable);


}
