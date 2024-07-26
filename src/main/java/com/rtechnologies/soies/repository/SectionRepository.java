package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByCampusIdAndGrade(Long id, String grade);
    List<Section> findByCampusId(Long campusId);

    Optional<Section> findByCampusIdAndSectionNameIgnoreCaseAndGrade(Long campusId, String sectionName, String grade);
}
