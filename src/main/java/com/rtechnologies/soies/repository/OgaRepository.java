package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Oga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OgaRepository extends JpaRepository<Oga, Long> {
    List<Oga> findByCourseId(Long courseId);
}
