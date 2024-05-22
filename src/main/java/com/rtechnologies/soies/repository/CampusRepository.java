package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    Optional<Campus> findByCampusName(String campusName);

    Optional<Campus> findByCampusNameIgnoreCase(String campusName);
}
