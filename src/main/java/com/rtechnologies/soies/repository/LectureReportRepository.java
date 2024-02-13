package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.LectureReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureReportRepository extends JpaRepository<LectureReport, Long> {
    Optional<LectureReport> findByStudentRollNumberAndLectureId(String studentRollNum, Long lectureId);
    List<LectureReport> findAllByLectureId(long lectureId);
}
