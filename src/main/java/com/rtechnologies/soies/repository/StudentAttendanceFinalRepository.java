package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.association.StudentAttendanceFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAttendanceFinalRepository extends JpaRepository<StudentAttendanceFinal, Long> {
}
