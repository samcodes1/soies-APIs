package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.TeacherSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSectionRepository extends JpaRepository<TeacherSection,Long> {
    List<TeacherSection> findByTeacherId(Long teacherId);
//    Optional<TeacherSection> findByTeacherIdAndSectionId(Long teacherId, Long id);


}
