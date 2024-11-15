package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.AssignmentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    Page<AssignmentSubmission> findByAssignmentId(Long assignmentId, Pageable pageable);

    List<AssignmentSubmission> findByAssignmentIdAndStudentRollNumber(Long assignmentId, String studentRollNumber);
    List<AssignmentSubmission> findByCourseId(Long courseId);
    List<AssignmentSubmission> findByStudentRollNumber(String studentRollNumber);

    @Query("SELECT a FROM AssignmentSubmission a WHERE a.studentRollNumber = :studentRollNumber AND a.term = :term")
    Page<AssignmentSubmission> findByStudentRollNumberAndTerm(@Param("studentRollNumber") String studentRollNumber, @Param("term") String term, Pageable pageable);
    Page<AssignmentSubmission> findByCourseIdAndTerm(Long courseId, String term, PageRequest pageable);
    Page<AssignmentSubmission> findByStudentRollNumber(String studentRollNumber, Pageable pageable);
    List<AssignmentSubmission> findByStudentRollNumberAndTerm(String rollNumber, String term);



}
