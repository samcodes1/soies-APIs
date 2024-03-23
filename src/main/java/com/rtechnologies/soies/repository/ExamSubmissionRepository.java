package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.ExamSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    List<ExamSubmission> findByStudentRollNumber(String studentRollNumber);

    Page<ExamSubmission> findByStudentRollNumberAndTerm(String studentRollNumber, String term, Pageable pageable);

    Page<ExamSubmission> findByCourseIdAndTerm(Long courseId, String term, PageRequest pageable);

    List<ExamSubmission> findByExamId(Long examId);
}
