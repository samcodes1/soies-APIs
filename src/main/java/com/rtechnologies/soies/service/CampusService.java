package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Campus;
import com.rtechnologies.soies.model.Section;
import com.rtechnologies.soies.repository.CampusRepository;
import com.rtechnologies.soies.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class CampusService {
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private SectionRepository sectionRepository;

    public Campus createCampus(Campus campus){
        Optional<Campus> campusOptional = campusRepository.findByCampusNameIgnoreCase(campus.getCampusName());

        if(campusOptional.isPresent()) {
            throw new RuntimeException("Campus already exists");
        }

        return campusRepository.save(campus);
    }

    public List<Campus> getAllCampuses(){
        return campusRepository.findAll();
    }

    public List<Section> getSectionsByCampusNameAndGrade(Long campusId, String grade) {
        Campus campus = campusRepository.findById(campusId)
                .orElseThrow(() -> new NotFoundException("Campus not found"));
        return sectionRepository.findByCampusIdAndGrade(campus.getId(), grade.toLowerCase());
    }

    public Section createSection(Section section) {
        // Check if the section with the same name, campus ID, and grade already exists
        sectionRepository.findByCampusIdAndSectionNameIgnoreCaseAndGrade(section.getCampusId(), section.getSectionName(), section.getGrade())
                .ifPresent(s -> {
                    throw new RuntimeException("Section with name " + section.getSectionName() +
                            " already exists for campus ID " + section.getCampusId() +
                            " and grade " + section.getGrade());
                });
        // Normalize the grade to lower case to maintain consistency
        section.setGrade(section.getGrade().toLowerCase());
        // Save the new section
        return sectionRepository.save(section);
    }
}