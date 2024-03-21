package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Oga;
import com.rtechnologies.soies.model.OgaQuestion;
import com.rtechnologies.soies.model.dto.CreateOgaRequest;
import com.rtechnologies.soies.model.dto.OgaListResponse;
import com.rtechnologies.soies.model.dto.OgaRequest;
import com.rtechnologies.soies.model.dto.OgaResponse;
import com.rtechnologies.soies.model.association.OgaSubmission;
import com.rtechnologies.soies.model.association.OgaStudentAnswer;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.OgaQuestionRepository;
import com.rtechnologies.soies.repository.OgaRepository;
import com.rtechnologies.soies.repository.OgaStudentAnswerRepository;
import com.rtechnologies.soies.repository.OgaSubmissionRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class OgaService {

    @Autowired
    private OgaRepository ogaRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private OgaQuestionRepository ogaQuestionRepository;

    @Autowired
    private OgaSubmissionRepository ogaSubmissionRepository;

    @Autowired
    private OgaStudentAnswerRepository ogaStudentAnswerRepository;

    public OgaResponse createOga(CreateOgaRequest ogaRequest) {
        Utility.printDebugLogs("OGA creation request: " + ogaRequest.toString());
        OgaResponse ogaResponse;

        try {
            if (ogaRequest == null) {
                Utility.printDebugLogs("OGA creation request is null");
                throw new IllegalArgumentException("Corrupt -data received");
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(ogaRequest.getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + ogaRequest.getCourseId());
                throw new NotFoundException("No course found with ID: " + ogaRequest.getCourseId());
            }

            Oga createdOga = mapToOga(ogaRequest);
            Utility.printDebugLogs("OGA created successfully: " + createdOga);

            for (int i = 0; i < ogaRequest.getOgaQuestions().size(); i++) {
                ogaRequest.getOgaQuestions().get(i).setOgaId(createdOga.getOgaId());
            }

            ogaQuestionRepository.saveAll(ogaRequest.getOgaQuestions());

            ogaResponse = OgaResponse.builder()
                    .ogaId(createdOga.getOgaId())
                    .ogaTitle(createdOga.getOgaTitle())
                    .description(createdOga.getDescription())
                    .totalMarks(createdOga.getTotalMarks())
                    .visibility(createdOga.isVisibility())
                    .ogaQuestions(ogaRequest.getOgaQuestions())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("OGA response: " + ogaResponse);
            return ogaResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public Oga mapToOga(CreateOgaRequest createOgaRequest) {
        return ogaRepository.save(Oga.builder()
                .courseId(createOgaRequest.getCourseId())
                .ogaTitle(createOgaRequest.getOgaTitle())
                .description(createOgaRequest.getDescription())
                .dueDate(createOgaRequest.getDueDate())
                .time(createOgaRequest.getTime())
                .totalMarks(createOgaRequest.getTotalMarks())
                .visibility(createOgaRequest.isVisibility())
                .build());
    }

    public OgaResponse updateOga(OgaRequest ogaRequest) {
        Utility.printDebugLogs("OGA update request: " + ogaRequest.toString());
        OgaResponse ogaResponse;

        try {
            if (ogaRequest == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for OGA
            Optional<Oga> existingOga = ogaRepository.findById(ogaRequest.getOgaId());
            if (existingOga.isEmpty()) {
                throw new NotFoundException("No OGA found with ID: " + ogaRequest.getOgaId());
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(ogaRequest.getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + ogaRequest.getCourseId());
                throw new NotFoundException("No course found with ID: " + ogaRequest.getCourseId());
            }

            Oga updatedOga = mapToOga(ogaRequest);
            Utility.printDebugLogs("OGA updated successfully: " + updatedOga);

            ogaQuestionRepository.saveAll(ogaRequest.getOgaQuestions());

            ogaResponse = OgaResponse.builder()
                    .ogaId(updatedOga.getOgaId())
                    .ogaTitle(updatedOga.getOgaTitle())
                    .description(updatedOga.getDescription())
                    .totalMarks(updatedOga.getTotalMarks())
                    .visibility(updatedOga.isVisibility())
                    .ogaQuestions(ogaRequest.getOgaQuestions())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("OGA update response: " + ogaResponse);
            return ogaResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    private Oga mapToOga(OgaRequest ogaRequest) {
        return ogaRepository.save(Oga.builder()
                .courseId(ogaRequest.getCourseId())
                .ogaTitle(ogaRequest.getOgaTitle())
                .description(ogaRequest.getDescription())
                .dueDate(ogaRequest.getDueDate())
                .totalMarks(ogaRequest.getTotalMarks())
                .visibility(ogaRequest.isVisibility())
                .build());
    }

    public OgaResponse deleteOga(Long ogaId) {
        Utility.printDebugLogs("OGA deletion request: " + ogaId);
        OgaResponse ogaResponse;

        try {
            Optional<Oga> existingOga = ogaRepository.findById(ogaId);

            if (existingOga.isEmpty()) {
                throw new NotFoundException("No OGA found with ID: " + ogaId);
            }

            ogaRepository.deleteById(ogaId);
            Utility.printDebugLogs("OGA deleted successfully: " + existingOga.get());

            ogaResponse = OgaResponse.builder()
                    .ogaId(existingOga.get().getOgaId())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("OGA response: " + ogaResponse);
            return ogaResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public OgaResponse getOgaById(Long ogaId) {
        Utility.printDebugLogs("Get OGA by ID: " + ogaId);
        OgaResponse ogaResponse;

        try {
            Optional<Oga> optionalOga = ogaRepository.findById(ogaId);

            if (optionalOga.isEmpty()) {
                Utility.printDebugLogs("No OGA found with ID: " + ogaId);
                throw new NotFoundException("No OGA found with ID: " + ogaId);
            }

            Optional<Course> optionalCourse = courseRepository.findById(optionalOga.get().getCourseId());
            if (optionalCourse.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + optionalOga.get().getCourseId());
                throw new NotFoundException("No course found with ID: " + optionalOga.get().getCourseId());
            }

            Oga oga = optionalOga.get();
            List<OgaQuestion> ogaQuestions = ogaQuestionRepository.findByOgaId(ogaId);

            ogaResponse = OgaResponse.builder()
                    .ogaId(oga.getOgaId())
                    .course(optionalCourse.get())
                    .ogaTitle(oga.getOgaTitle())
                    .description(oga.getDescription())
                    .ogaQuestions(ogaQuestions)
                    .dueDate(oga.getDueDate())
                    .totalMarks(oga.getTotalMarks())
                    .time(oga.getTime())
                    .visibility(oga.isVisibility())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("OGA response: " + ogaResponse);
            return ogaResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return OgaResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public OgaListResponse getOgasByCourseId(Long courseId) {
        Utility.printDebugLogs("Get OGAs by course ID: " + courseId);
        OgaListResponse ogaListResponse;

        try {
            List<Oga> ogaList = ogaRepository.findByCourseId(courseId);

            if (ogaList.isEmpty()) {
                Utility.printDebugLogs("No OGAs found for course ID: " + courseId);
                throw new NotFoundException("No OGAs found for course ID: " + courseId);
            }

            ogaListResponse = OgaListResponse.builder()
                    .ogaList(ogaList)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("OGA list response: " + ogaListResponse);
            return ogaListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return OgaListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return OgaListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

}
