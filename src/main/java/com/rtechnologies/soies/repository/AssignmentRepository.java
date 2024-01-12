package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByTeacherId(Long teacherId);
    List<Assignment> findByCourseId(Long courseId);
}
