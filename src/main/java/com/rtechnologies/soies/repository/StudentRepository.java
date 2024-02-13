package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(String rollNum);
    void deleteByRollNumber(String rollNum);
    Page<Student> findAllByCampusName(String campusName, PageRequest pageRequest);
}
