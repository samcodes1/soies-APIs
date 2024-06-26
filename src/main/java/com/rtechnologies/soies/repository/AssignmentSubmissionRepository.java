package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.AssignmentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    Page<AssignmentSubmission> findByAssignmentId(Long assignmentId, Pageable pageable);

    List<AssignmentSubmission> findByAssignmentIdAndStudentRollNumber(Long assignmentId, String studentRollNumber);

    List<AssignmentSubmission> findByStudentRollNumber(String studentRollNumber);

    Page<AssignmentSubmission> findByStudentRollNumberAndTerm(String studentRollNumber, String term, Pageable pageable);

    Page<AssignmentSubmission> findByCourseIdAndTerm(Long courseId, String term, PageRequest pageable);
}
