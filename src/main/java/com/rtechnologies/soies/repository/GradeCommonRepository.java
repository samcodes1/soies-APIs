package com.rtechnologies.soies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtechnologies.soies.model.GradeCommon;

@Repository
public interface GradeCommonRepository extends JpaRepository<GradeCommon, Long> {
    
}
