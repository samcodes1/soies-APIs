package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.TeacherSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSectionRepository extends JpaRepository<TeacherSection,Long> {
    List<TeacherSection> findByTeacherId(Long teacherId);
}
