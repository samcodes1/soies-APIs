package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.ExamStudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamStudentAnswerRepository extends JpaRepository<ExamStudentAnswer, Long> {
}
