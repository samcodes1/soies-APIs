package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.QuizSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.quizId = :quizId")
    List<QuizSubmission> findByQuizId(@Param("quizId") Long quizId);
    List<QuizSubmission> findByStudentRollNumber(String studentRollNumber);
    List<QuizSubmission> findByCourseId(Long courseId);

    Page<QuizSubmission> findByStudentRollNumberAndTerm(String studentRollNumber, String term, Pageable pageable);
    List<QuizSubmission> findByCourseIdAndStudentRollNumber(Long courseId, String studentRollNumber);

    Page<QuizSubmission> findByCourseIdAndTerm(Long courseId, String term, PageRequest pageable);
}
