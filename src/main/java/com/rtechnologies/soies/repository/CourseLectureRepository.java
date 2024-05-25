package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.CourseLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseLectureRepository extends JpaRepository<CourseLecture, Long> {
}
