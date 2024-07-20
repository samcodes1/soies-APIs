package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.*;
import com.rtechnologies.soies.model.association.ExamStudentAnswer;
import com.rtechnologies.soies.model.association.ExamSubmission;
import com.rtechnologies.soies.model.association.OgaSubmission;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.*;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamQuestionRepository examQuestionRepository;

    @Autowired
    private ExamSubmissionRepository examSubmissionRepository;

    @Autowired
    private ExamStudentAnswerRepository examStudentAnswerRepository;

    public ExamResponse createExam(CreateExamRequest exam) {
        Utility.printDebugLogs("Exam creation request: " + exam.toString());
        ExamResponse examResponse;

        try {
            if (exam == null) {
                Utility.printDebugLogs("Exam creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(exam.getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + exam.getCourseId());
                throw new NotFoundException("No course found with ID: " + exam.getCourseId());
            }

            Exam createExam = mapToQuiz(exam);
            Utility.printDebugLogs("Exam created successfully: " + createExam);

            for (int i = 0; i < exam.getExamQuestions().size(); i++) {
                exam.getExamQuestions().get(i).setExamId(createExam.getExamId());
            }

            examQuestionRepository.saveAll(exam.getExamQuestions());

            examResponse = ExamResponse.builder()
                    .examId(createExam.getExamId())
                    .examTitle(createExam.getExamTitle())
                    .description(createExam.getDescription())
                    .totalMarks(createExam.getTotalMarks())
                    .visibility(createExam.isVisibility())
                    .examQuestions(exam.getExamQuestions())
                    .dueDate(exam.getDueDate())
                    .totalMarks(exam.getTotalMarks())
                    .time(exam.getTime())
                    .visibility(exam.isVisibility())
                    .description(exam.getDescription())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Exam response: " + examResponse);
            return examResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public Exam mapToQuiz(CreateExamRequest createExamRequest) {
        return examRepository.save(Exam.builder()
                .courseId(createExamRequest.getCourseId())
                .examTitle(createExamRequest.getExamTitle())
                .description(createExamRequest.getDescription())
                .dueDate(createExamRequest.getDueDate())
                .time(createExamRequest.getTime())
                .totalMarks(createExamRequest.getTotalMarks())
                .visibility(createExamRequest.isVisibility())
                .term(createExamRequest.getTerm())
                .build());
    }

    public Exam mapToQuiz(ExamRequest createExamRequest) {
        return examRepository.save(Exam.builder()
                .courseId(createExamRequest.getCourseId())
                .examTitle(createExamRequest.getExamTitle())
                .description(createExamRequest.getDescription())
                .dueDate(createExamRequest.getDueDate())
                .time(createExamRequest.getTime())
                .totalMarks(createExamRequest.getTotalMarks())
                .term(createExamRequest.getTerm())
                .visibility(createExamRequest.isVisibility())
                .build());
    }

    public ExamResponse updateExam(ExamRequest examRequest) {
        Utility.printDebugLogs("Exam update request: " + examRequest.toString());
        ExamResponse examResponse;

        try {
            if (examRequest == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for existing exam
            Optional<Exam> existingExamOptional = examRepository.findById(examRequest.getExamId());
            if (existingExamOptional.isEmpty()) {
                throw new NotFoundException("No Exam found with ID: " + examRequest.getExamId());
            }

            // Check for existing course
            Optional<Course> courseOptional = courseRepository.findById(examRequest.getCourseId());
            if (courseOptional.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + examRequest.getCourseId());
                throw new NotFoundException("No course found with ID: " + examRequest.getCourseId());
            }

            // Update the exam entity
            Exam updatedExam = mapToExam(examRequest);
            updatedExam.setExamId(existingExamOptional.get().getExamId()); // Retain the original exam ID
            examRepository.save(updatedExam);

            // Get existing questions
            List<ExamQuestion> existingQuestions = examQuestionRepository.findByExamId(updatedExam.getExamId());

            // Separate existing questions that are to be kept
            Set<Long> updatedQuestionIds = examRequest.getExamQuestions().stream()
                    .map(ExamQuestion::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Determine which existing questions are to be deleted
            List<ExamQuestion> questionsToDelete = existingQuestions.stream()
                    .filter(question -> !updatedQuestionIds.contains(question.getId()))
                    .collect(Collectors.toList());

            // Delete the old questions
            if (!questionsToDelete.isEmpty()) {
                examQuestionRepository.deleteAll(questionsToDelete);
            }

            // Process updated questions
            List<ExamQuestion> newQuestions = new ArrayList<>();
            List<ExamQuestion> savedUpdatedQuestions = new ArrayList<>();
            for (ExamQuestion question : examRequest.getExamQuestions()) {
                if (question.getId() != null) {
                    // Update existing questions
                    Optional<ExamQuestion> existingQuestion = examQuestionRepository.findById(question.getId());
                    if (existingQuestion.isPresent()) {
                        ExamQuestion questionToUpdate = existingQuestion.get();
                        questionToUpdate.setQuestion(question.getQuestion());
                        questionToUpdate.setOptionOne(question.getOptionOne());
                        questionToUpdate.setOptionTwo(question.getOptionTwo());
                        questionToUpdate.setOptionThree(question.getOptionThree());
                        questionToUpdate.setOptionFour(question.getOptionFour());
                        questionToUpdate.setAnswer(question.getAnswer());
                        questionToUpdate.setExamId(updatedExam.getExamId());
                        savedUpdatedQuestions.add(examQuestionRepository.save(questionToUpdate));
                    }
                } else {
                    // Add new questions
                    ExamQuestion questionToAdd = new ExamQuestion();
                    questionToAdd.setQuestion(question.getQuestion());
                    questionToAdd.setOptionOne(question.getOptionOne());
                    questionToAdd.setOptionTwo(question.getOptionTwo());
                    questionToAdd.setOptionThree(question.getOptionThree());
                    questionToAdd.setOptionFour(question.getOptionFour());
                    questionToAdd.setAnswer(question.getAnswer());
                    questionToAdd.setExamId(updatedExam.getExamId());
                    newQuestions.add(questionToAdd);
                }
            }

            // Save new questions
            List<ExamQuestion> savedNewQuestions = examQuestionRepository.saveAll(newQuestions);

            // Build response
            examResponse = ExamResponse.builder()
                    .examId(updatedExam.getExamId())
                    .examTitle(updatedExam.getExamTitle())
                    .description(updatedExam.getDescription())
                    .totalMarks(updatedExam.getTotalMarks())
                    .visibility(updatedExam.isVisibility())
                    .course(courseOptional.get()) // Include course details
                    .examQuestions(new ArrayList<>(savedUpdatedQuestions) {{
                        addAll(savedNewQuestions);
                    }})
                    .visibility(updatedExam.isVisibility())
                    .description(updatedExam.getDescription())
                    .time(updatedExam.getTime())
                    .totalMarks(updatedExam.getTotalMarks())
                    .dueDate(updatedExam.getDueDate())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Exam response: " + examResponse);
            return examResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    private Exam mapToExam(ExamRequest examRequest) {
        return Exam.builder()
                .examId(examRequest.getExamId()) // Retain the existing exam ID
                .courseId(examRequest.getCourseId())
                .examTitle(examRequest.getExamTitle())
                .description(examRequest.getDescription())
                .dueDate(examRequest.getDueDate())
                .time(examRequest.getTime())
                .totalMarks(examRequest.getTotalMarks())
                .term(examRequest.getTerm())
                .visibility(examRequest.isVisibility())
                .build();
    }


    public ExamResponse deleteExam(Long examId) {
        Utility.printDebugLogs("Exam deletion request: " + examId);
        ExamResponse examResponse;

        try {
            Optional<Exam> existingExam = examRepository.findById(examId);

            if (existingExam.isEmpty()) {
                throw new NotFoundException("No exam found with ID: " + examId);
            }

            examRepository.deleteById(examId);
            Utility.printDebugLogs("Exam deleted successfully: " + existingExam.get());

            examResponse = ExamResponse.builder()
                    .examId(existingExam.get().getExamId())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Exam response: " + examResponse);
            return examResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public ExamListResponse getExamsByCourseId(Long courseId, String studentRollNum) {
        Utility.printDebugLogs("Get exams by course ID: " + courseId);
        ExamListResponse examListResponse;

        try {
            List<Exam> examList = examRepository.findAllByCourseId(courseId);

            if (examList.isEmpty()) {
                Utility.printDebugLogs("No exams found for course ID: " + courseId);
                throw new NotFoundException("No exams found for course ID: " + courseId);
            }

            List<Exam> finalList = new ArrayList<>();
            List<ExamSubmission> quizSubmissions = examSubmissionRepository.findByStudentRollNumber(studentRollNum);
            finalList = examList;
            if (!quizSubmissions.isEmpty()) {
                for (int i = 0; i < quizSubmissions.size(); i++) {
                    for (Exam exam : examList) {
                        if (Objects.equals(exam.getExamId(), quizSubmissions.get(i).getExamId())) {
                            finalList.remove(exam);
                            break;
                        }
                    }
                }
                examListResponse = ExamListResponse.builder()
                        .examList(finalList)
                        .messageStatus("Success")
                        .build();
            } else {
                examListResponse = ExamListResponse.builder()
                        .examList(examList)
                        .messageStatus("Success")
                        .build();
            }

            Utility.printDebugLogs("Exam list response: " + examListResponse);
            return examListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return ExamListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return ExamListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public ExamResponse getExamById(Long examId) {
        Utility.printDebugLogs("Get exam by ID: " + examId);
        ExamResponse examResponse;

        try {
            Optional<Exam> optionalExam = examRepository.findById(examId);

            if (optionalExam.isEmpty()) {
                Utility.printDebugLogs("No exam found with ID: " + examId);
                throw new NotFoundException("No exam found with ID: " + examId);
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(optionalExam.get().getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + optionalExam.get().getCourseId());
                throw new NotFoundException("No course found with ID: " + optionalExam.get().getCourseId());
            }

            Exam exam = optionalExam.get();
            List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(examId);
            examResponse = ExamResponse.builder()
                    .examId(exam.getExamId())
                    .examTitle(exam.getExamTitle())
                    .description(exam.getDescription())
                    .totalMarks(exam.getTotalMarks())
                    .visibility(exam.isVisibility())
                    .course(course.get())
                    .examQuestions(examQuestions)
                    .dueDate(exam.getDueDate())
                    .visibility(exam.isVisibility())
                    .description(exam.getDescription())
                    .time(exam.getTime())
                    .totalMarks(exam.getTotalMarks())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Exam response: " + examResponse);
            return examResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return ExamResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public String submitExam(ExamSubmissionRequest examSubmissionRequest) {
        ExamSubmission examSubmission = new ExamSubmission();
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(examSubmissionRequest.getExamId());

        if (examQuestions.isEmpty()) {
            throw new NotFoundException("No Exam found with ID: " + examSubmissionRequest.getExamId());
        }

        examSubmission = mapToExamSubmission(examSubmissionRequest);
        examSubmissionRepository.save(examSubmission);

        int totalMarks = examSubmission.getTotalMarks();
        int perQuestionMark = totalMarks / examQuestions.size();
        int gainedMarks = 0;

        // Save answers to the DB
        for (int i = 0; i < examSubmissionRequest.getExamQuestionList().size(); i++) {
            boolean isCorrect = false;

            if (examSubmissionRequest.getExamQuestionList()
                    .get(i).getAnswer().equals(examQuestions.get(i).getAnswer())) {
                gainedMarks += perQuestionMark;
                isCorrect = true;
            }

            examStudentAnswerRepository.save(ExamStudentAnswer.builder()
                    .examSubmissionId(examSubmission.getExamId())
                    .questionId(examSubmission.getId())
                    .answer(examSubmissionRequest.getExamQuestionList()
                            .get(i).getAnswer())
                    .isCorrect(isCorrect)
                    .build());
        }

        double percentage = (double) gainedMarks / totalMarks * 100;
        examSubmission.setGainedMarks(gainedMarks);
        examSubmission.setPercentage(percentage);

        examSubmissionRepository.save(examSubmission);

        return "Exam submitted successfully";
    }

    public ExamSubmissionListResponse getAllExamSubmission(Long examId) {
        List<ExamSubmission> submittedExams = examSubmissionRepository.findByExamId(examId);
        ExamSubmissionListResponse examSubmissionListResponse = new ExamSubmissionListResponse();
        if (submittedExams.isEmpty()) {
            throw new NotFoundException("No Exam found with ID: " + examId);
        }

        examSubmissionListResponse.setExamSubmissionList(submittedExams);
        examSubmissionListResponse.setMessageStatus("Success");

        return examSubmissionListResponse;
    }

    public ExamResponse getAllExams(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Exam> submittedExams = examRepository.findAll(pageable);
        ExamResponse examSubmissionListResponse = new ExamResponse();
        if (submittedExams.isEmpty()) {
            throw new NotFoundException("No Exam found with ID: ");
        }

        examSubmissionListResponse.setExamListingPage(submittedExams);
        examSubmissionListResponse.setMessageStatus("Success");

        return examSubmissionListResponse;
    }

    public static ExamSubmission mapToExamSubmission(ExamSubmissionRequest submissionRequest) {
        return ExamSubmission.builder()
                .examId(submissionRequest.getExamId())
                .courseId(submissionRequest.getCourseId())
                .studentRollNumber(submissionRequest.getStudentRollNumber())
                .totalMarks(submissionRequest.getTotalMarks())
                .gainedMarks(0)
                .build();
    }

    public ExamResponse getAllExamsByCourseId(Long courseid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Exam> submittedExams = examRepository.findAllByCourseId(courseid, pageable);
        ExamResponse examSubmissionListResponse = new ExamResponse();
        examSubmissionListResponse.setExamListingPage(submittedExams);
        examSubmissionListResponse.setMessageStatus("Success");
        return examSubmissionListResponse;
    }
}
