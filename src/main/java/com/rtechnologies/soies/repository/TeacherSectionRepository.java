package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.TeacherSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherSectionRepository extends JpaRepository<TeacherSection,Long> {
}
