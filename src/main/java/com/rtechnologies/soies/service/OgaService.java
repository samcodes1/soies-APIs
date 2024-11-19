package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.*;
import com.rtechnologies.soies.model.association.AssignmentSubmission;
import com.rtechnologies.soies.model.association.QuizSubmission;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.model.association.OgaSubmission;
import com.rtechnologies.soies.model.association.OgaStudentAnswer;
import com.rtechnologies.soies.repository.*;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

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
                .term(createOgaRequest.getTerm())
                .build());
    }

    @Transactional
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

            // Check for course
            Optional<Course> course = courseRepository.findById(ogaRequest.getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + ogaRequest.getCourseId());
                throw new NotFoundException("No course found with ID: " + ogaRequest.getCourseId());
            }

            // Retrieve existing questions for the OGA
            List<OgaQuestion> existingQuestions = ogaQuestionRepository.findByOgaId(ogaRequest.getOgaId());

            // Update the OGA entity
            Oga updatedOga = existingOga.get();
            updatedOga.setOgaTitle(ogaRequest.getOgaTitle() != null ? ogaRequest.getOgaTitle() : updatedOga.getOgaTitle());
            updatedOga.setDescription(ogaRequest.getDescription() != null ? ogaRequest.getDescription() : updatedOga.getDescription());
            updatedOga.setDueDate(ogaRequest.getDueDate() != null ? ogaRequest.getDueDate() : updatedOga.getDueDate());
            updatedOga.setTotalMarks(ogaRequest.getTotalMarks() > 0 ? ogaRequest.getTotalMarks() : updatedOga.getTotalMarks());
            updatedOga.setTime(ogaRequest.getTime() != null ? ogaRequest.getTime() : updatedOga.getTime());
            updatedOga.setTerm(ogaRequest.getTerm() != null ? ogaRequest.getTerm() : updatedOga.getTerm());

            // Handle visibility update (if provided)
            if (ogaRequest.getVisibility() != null) {
                updatedOga.setVisibility(ogaRequest.getVisibility());
            }

            // Save the updated OGA entity
            ogaRepository.save(updatedOga);

            // Process OGA questions (existing logic retained)
            List<OgaQuestion> updatedQuestions = new ArrayList<>();
            List<OgaQuestion> newQuestions = new ArrayList<>();
            List<Long> incomingQuestionIds = ogaRequest.getOgaQuestions().stream()
                    .map(OgaQuestion::getId)
                    .collect(Collectors.toList());

            for (OgaQuestion newQuestion : ogaRequest.getOgaQuestions()) {
                if (newQuestion.getId() != null) {
                    // Check if the question already exists
                    Optional<OgaQuestion> existingQuestion = ogaQuestionRepository.findById(newQuestion.getId());
                    if (existingQuestion.isPresent()) {
                        // Update existing question
                        OgaQuestion questionToUpdate = existingQuestion.get();
                        questionToUpdate.setQuestion(newQuestion.getQuestion());
                        questionToUpdate.setOgaId(ogaRequest.getOgaId());
                        questionToUpdate.setOptionOne(newQuestion.getOptionOne());
                        questionToUpdate.setOptionTwo(newQuestion.getOptionTwo());
                        questionToUpdate.setOptionThree(newQuestion.getOptionThree());
                        questionToUpdate.setOptionFour(newQuestion.getOptionFour());
                        questionToUpdate.setAnswer(newQuestion.getAnswer());
                        updatedQuestions.add(questionToUpdate);
                    }
                } else {
                    // Add new question
                    OgaQuestion questionToAdd = new OgaQuestion();
                    questionToAdd.setQuestion(newQuestion.getQuestion());
                    questionToAdd.setOgaId(ogaRequest.getOgaId());
                    questionToAdd.setOptionOne(newQuestion.getOptionOne());
                    questionToAdd.setOptionTwo(newQuestion.getOptionTwo());
                    questionToAdd.setOptionThree(newQuestion.getOptionThree());
                    questionToAdd.setOptionFour(newQuestion.getOptionFour());
                    questionToAdd.setAnswer(newQuestion.getAnswer());
                    newQuestions.add(questionToAdd);
                }
            }

            // Identify and remove old questions not in the update request
            List<OgaQuestion> questionsToRemove = existingQuestions.stream()
                    .filter(question -> !incomingQuestionIds.contains(question.getId()))
                    .collect(Collectors.toList());
            ogaQuestionRepository.deleteAll(questionsToRemove);

            // Save updated and new questions
            List<OgaQuestion> savedNewQuestions = ogaQuestionRepository.saveAll(newQuestions);
            ogaQuestionRepository.saveAll(updatedQuestions);

            // Build OGA response
            ogaResponse = OgaResponse.builder()
                    .ogaId(updatedOga.getOgaId())
                    .ogaTitle(updatedOga.getOgaTitle())
                    .description(updatedOga.getDescription())
                    .totalMarks(updatedOga.getTotalMarks())
                    .visibility(updatedOga.isVisibility())  // Include visibility in the response
                    .dueDate(updatedOga.getDueDate())
                    .time(updatedOga.getTime())
                    .term(updatedOga.getTerm())
                    .ogaQuestions(new ArrayList<>(updatedQuestions) {{
                        addAll(savedNewQuestions);
                    }})
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

    private Oga mapToOga(OgaRequest ogaRequest, Oga existingOga) {
        return Oga.builder()
                .ogaId(ogaRequest.getOgaId())
                .courseId(ogaRequest.getCourseId())
                .ogaTitle(ogaRequest.getOgaTitle() != null ? ogaRequest.getOgaTitle() : existingOga.getOgaTitle())
                .description(ogaRequest.getDescription() != null ? ogaRequest.getDescription() : existingOga.getDescription())
                .dueDate(ogaRequest.getDueDate() != null ? ogaRequest.getDueDate() : existingOga.getDueDate())
                .totalMarks(ogaRequest.getTotalMarks() > 0 ? ogaRequest.getTotalMarks() : existingOga.getTotalMarks())
                .visibility(ogaRequest.getVisibility() != null ? ogaRequest.getVisibility() : existingOga.isVisibility()) // Handle visibility update
                .term(ogaRequest.getTerm() != null ? ogaRequest.getTerm() : existingOga.getTerm())
                .time(ogaRequest.getTime() != null ? ogaRequest.getTime() : existingOga.getTime())
                .build();
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
                .term(oga.getTerm())
                .messageStatus("Success")
                .build();

        Utility.printDebugLogs("OGA response: " + ogaResponse);
        return ogaResponse;

    }

    public OgaDtoResposne getOgasByCourseId(Long courseId, String studentRollNum) {
        Utility.printDebugLogs("Get OGAs by course ID: " + courseId);
        OgaDtoResposne ogaListResponse;

        // Fetch OGAs by course ID
        List<Oga> ogaList = ogaRepository.findByCourseId(courseId);

        if (ogaList.isEmpty()) {
            Utility.printDebugLogs("No OGAs found for course ID: " + courseId);
            return OgaDtoResposne.builder()
                    .ogaList(Collections.emptyList())
                    .messageStatus("No OGAs found")
                    .build();
        }

        // Fetch OGA submissions by student roll number
        List<OgaSubmission> ogaSubmissions = ogaSubmissionRepository.findByStudentRollNumber(studentRollNum);

        // Create a Set of submitted OGA IDs
        Set<Long> submittedOgaIds = ogaSubmissions.stream()
                .map(OgaSubmission::getOgaId)
                .collect(Collectors.toSet());

        // Prepare the final list of OGAs with hasAttempted flag
        List<OgaDTO> ogaDTOList = ogaList.stream()
                .map(oga -> OgaDTO.builder()
                        .ogaId(oga.getOgaId())
                        .courseId(oga.getCourseId())
                        .ogaTitle(oga.getOgaTitle())
                        .description(oga.getDescription())
                        .dueDate(oga.getDueDate())
                        .term(oga.getTerm())
                        .visibility(oga.isVisibility())
                        .hasAttempted(submittedOgaIds.contains(oga.getOgaId()))  // Set the hasAttempted flag
                        .build())
                .collect(Collectors.toList());

        // Build the response
        ogaListResponse = OgaDtoResposne.builder()
                .ogaList(ogaDTOList)  // Return List<OgaDTO>
                .messageStatus("Success")
                .build();

        Utility.printDebugLogs("OGA list response: " + ogaListResponse);
        return ogaListResponse;
    }




    public String submitOga(OgaSubmissionRequest ogaSubmissionRequest) {
        OgaSubmission ogaSubmission = new OgaSubmission();

        // Fetch OGA by ID
        Optional<Oga> oga = ogaRepository.findById(ogaSubmissionRequest.getOgaId());
        if (oga.isEmpty()) {
            throw new NotFoundException("No OGA found with ID: " + ogaSubmissionRequest.getOgaId());
        }

        // Fetch OGA questions
        List<OgaQuestion> ogaQuestions = ogaQuestionRepository.findByOgaId(ogaSubmissionRequest.getOgaId());
        if (ogaQuestions.isEmpty()) {
            throw new NotFoundException("No questions found for OGA ID: " + ogaSubmissionRequest.getOgaId());
        }

        // Create and save the OGA submission
        ogaSubmission = mapToOgaSubmission(ogaSubmissionRequest);
        ogaSubmission.setHasAttempted(true);  // Mark the submission as attempted
        ogaSubmissionRepository.save(ogaSubmission);

        int totalMarks = ogaSubmission.getTotalMarks();
        int perQuestionMark = totalMarks / ogaQuestions.size();
        int gainedMarks = 0;

        // Save student answers to the DB
        for (int i = 0; i < ogaSubmissionRequest.getOgaQuestionList().size(); i++) {
            boolean isCorrect = false;

            if (ogaSubmissionRequest.getOgaQuestionList().get(i).getAnswer().equals(ogaQuestions.get(i).getAnswer())) {
                gainedMarks += perQuestionMark;
                isCorrect = true;
            }

            ogaStudentAnswerRepository.save(OgaStudentAnswer.builder()
                    .ogaSubmissionId(ogaSubmission.getOgaId())
                    .questionId(ogaSubmission.getId())
                    .answer(ogaSubmissionRequest.getOgaQuestionList().get(i).getAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        // Calculate percentage and update submission
        double percentage = (double) gainedMarks / totalMarks * 100;
        ogaSubmission.setGainedMarks(gainedMarks);
        ogaSubmission.setPercentage(percentage);
        ogaSubmission.setTerm(oga.get().getTerm());

        // Save the updated submission with marks and percentage
        ogaSubmissionRepository.save(ogaSubmission);

        return "OGA submitted successfully";
    }


    public OgaSubmissionListResponse getAllOgaSubmission(Long ogaId) {
        List<OgaSubmission> submittedOgas = ogaSubmissionRepository.findByOgaId(ogaId);
        OgaSubmissionListResponse ogaSubmissionListResponse = new OgaSubmissionListResponse();
        if (submittedOgas.isEmpty()) {
            throw new NotFoundException("No Oga found with ID: " + ogaId);
        }

        ogaSubmissionListResponse.setOgaSubmissionList(submittedOgas);
        ogaSubmissionListResponse.setMessageStatus("Success");

        return ogaSubmissionListResponse;
    }

    public static OgaSubmission mapToOgaSubmission(OgaSubmissionRequest submissionRequest) {
        return OgaSubmission.builder()
                .ogaId(submissionRequest.getOgaId())
                .courseId(submissionRequest.getCourseId())
                .studentRollNumber(submissionRequest.getStudentRollNumber())
                .totalMarks(submissionRequest.getTotalMarks())
                .gainedMarks(0)
                .build();
    }

    public OgaResponse getPageListing(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Oga> ogapagination = ogaRepository.findAll(pageable);
        OgaResponse response = new OgaResponse();
        response.setOgaPagination(ogapagination);
        response.setMessageStatus("Success");
        return response;
    }

    public OgaListResponse getAllOgaByCourseid(Long courseid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Oga> ogaPage = ogaRepository.findByCourseId(courseid, pageable);

        OgaListResponse ogaListResponse = new OgaListResponse();
        ogaListResponse.setOgaList(ogaPage.getContent());
        ogaListResponse.setMessageStatus("Success");

        return ogaListResponse;
    }

    public OgaSubmissionsResponse getOgaSubmissionsByCourseId(Long courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // Create Pageable instance
        Page<OgaSubmission> submissionsPage = ogaSubmissionRepository.findByCourseId(courseId, pageable);

        OgaSubmissionsResponse response = new OgaSubmissionsResponse();

        if (submissionsPage.isEmpty()) {
            response.setMessageStatus("No OGAs found for the given course ID");
            response.setOgaSubmissionList(Collections.emptyList()); // No OGAs found
        } else {
            response.setMessageStatus("OGAs retrieved successfully");

            List<OgaSubmission> consolidatedSubmissions = submissionsPage.getContent().stream()
                    .collect(Collectors.groupingBy(
                            submission -> Arrays.asList(submission.getStudentRollNumber(), submission.getOgaId()), // Group by studentRollNumber and ogaId
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    submissions -> {
                                        OgaSubmission consolidated = new OgaSubmission();
                                        consolidated.setStudentRollNumber(submissions.get(0).getStudentRollNumber());
                                        consolidated.setCourseId(submissions.get(0).getCourseId());
                                        consolidated.setOgaId(submissions.get(0).getOgaId());
                                        consolidated.setTerm(submissions.get(0).getTerm());

                                        int totalMarks = submissions.get(0).getTotalMarks();

                                        double avgMarks = submissions.stream()
                                                .mapToInt(OgaSubmission::getGainedMarks)
                                                .average()
                                                .orElse(0);
                                        double percentage = totalMarks > 0 ? (avgMarks / totalMarks) * 100 : 0;

                                        consolidated.setTotalMarks(totalMarks);
                                        consolidated.setGainedMarks((int) avgMarks);
                                        consolidated.setPercentage(percentage);
                                        consolidated.setHasAttempted(true);
                                        return consolidated;
                                    }
                            )
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            response.setOgaSubmissionList(consolidatedSubmissions);
        }
        List<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository.findByCourseId(courseId);
        List<QuizSubmission> quizSubmissions = quizSubmissionRepository.findByCourseId(courseId);

        response.setAssignmentSubmissionList(assignmentSubmissions);
        response.setQuizSubmissionList(quizSubmissions);

        response.setCurrentPage(page);
        response.setTotalPages(1); 

        return response;
    }

}
