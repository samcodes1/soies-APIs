package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    StudentAttendance findFirstByStudentRollNumAndDateOrderByLastLoginTimeDesc(String rollNumber, LocalDate now);

    StudentAttendance findFirstByStudentRollNumOrderByDateDesc(String rollNumber);


    @Query("SELECT sa FROM StudentAttendance sa WHERE sa.studentRollNum = :rollNumber ORDER BY sa.date DESC")
    List<StudentAttendance> findLatestByStudentRollNum(@Param("rollNumber") String rollNumber);


}
