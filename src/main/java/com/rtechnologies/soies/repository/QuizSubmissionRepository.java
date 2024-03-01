package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.QuizQuestion;
import com.rtechnologies.soies.model.association.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByQuizId(Long quizId);
}
