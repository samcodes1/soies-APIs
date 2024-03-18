package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.QuizSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByQuizId(Long quizId);

    List<QuizSubmission> findByStudentRollNumber(String studentRollNumber);

    Page<QuizSubmission> findByStudentRollNumberAndTerm(String studentRollNumber, String term, Pageable pageable);

    Page<QuizSubmission> findByCourseIdAndTerm(Long courseId, String term, PageRequest pageable);
}
