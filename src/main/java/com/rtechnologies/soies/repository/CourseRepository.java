package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {
    List<Course> findByGrade(String grade);
}
