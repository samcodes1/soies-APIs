package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam,Long> {
    List<Exam> findAllByCourseId(long courseId);

    Page<Exam> findAllByCourseId(Long courseid, Pageable pageable);

}
