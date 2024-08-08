package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {
    List<Course> findByGrade(String grade);
    Optional<Course> findByCourseNameIgnoreCaseAndGrade(String courseName, String grade);
    List<Course> findCoursesByGrade(String grade);


}
