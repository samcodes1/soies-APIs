package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.QuizStudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizStudentAnswerRepository extends JpaRepository<QuizStudentAnswer, Long> {
}
