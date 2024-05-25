package com.rtechnologies.soies.repository;

import com.rtechnologies.soies.model.OgaQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OgaQuestionRepository extends JpaRepository<OgaQuestion, Long> {
    List<OgaQuestion> findByOgaId(Long ogaId);
}
