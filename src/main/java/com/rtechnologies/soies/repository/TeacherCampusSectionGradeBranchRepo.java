package com.rtechnologies.soies.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rtechnologies.soies.model.association.TeacherCampusSectionGradeBranch;

public interface TeacherCampusSectionGradeBranchRepo  extends JpaRepository<TeacherCampusSectionGradeBranch, Long>{
    Optional<TeacherCampusSectionGradeBranch> findByTeacheIdFkAndSectionIdFk(Long teacherid, Long sectionid);
    List<TeacherCampusSectionGradeBranch> findByTeacheIdFk(Long teacherId);

}
