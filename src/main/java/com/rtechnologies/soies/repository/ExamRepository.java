package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Exam;
import com.rtechnologies.soies.model.Lecture;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam,Long> {
    List<Exam> findAllByCourseId(long courseId);
}
