package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.OgaStudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OgaStudentAnswerRepository extends JpaRepository<OgaStudentAnswer, Long> {
}
