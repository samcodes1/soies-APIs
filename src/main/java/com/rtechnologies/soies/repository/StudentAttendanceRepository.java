package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance,Long > {
    List<StudentAttendance> findByStudentRollNumAndDate(String rollNumber, LocalDate now);
}
