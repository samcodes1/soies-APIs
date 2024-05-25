package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findAllByCourseId(long courseId);
}
