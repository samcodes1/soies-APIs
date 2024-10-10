package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.OgaSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OgaSubmissionRepository extends JpaRepository<OgaSubmission, Long> {
    List<OgaSubmission> findByStudentRollNumber(String studentRollNumber);

    Page<OgaSubmission> findByStudentRollNumberAndTerm(String studentRollNumber, String term, Pageable pageable);

    Page<OgaSubmission> findByCourseId(Long courseId, Pageable pageable);


    List<OgaSubmission> findByOgaId(Long ogaId);
    List<OgaSubmission> findByCourseIdAndStudentRollNumber(Long courseId, String studentRollNumber);

}
