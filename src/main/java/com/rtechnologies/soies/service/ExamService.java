package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.*;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.ExamQuestionRepository;
import com.rtechnologies.soies.repository.ExamRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

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
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + exam.getCourseId());
                throw new NotFoundException("No course found with ID: " + exam.getCourseId());
            }

            Exam createExam = mapToQuiz(exam);
            Utility.printDebugLogs("Exam created successfully: " + createExam);

            for(int i=0; i<exam.getExamQuestions().size(); i++){
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
                .visibility(createExamRequest.isVisibility())
                .build());
    }
    public ExamResponse updateExam(ExamRequest exam) {
        Utility.printDebugLogs("Exam update request: " + exam.toString());
        ExamResponse examResponse;

        try {
            if (exam == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for exam
            Optional<Exam> existingExam = examRepository.findById(exam.getExamId());
            if (existingExam.isEmpty()) {
                throw new NotFoundException("No Exam found with ID: " + exam.getExamId());
            }


            // Check for course
            Optional<Course> course = courseRepository.findById(exam.getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + exam.getCourseId());
                throw new NotFoundException("No course found with ID: " + exam.getCourseId());
            }

            Exam updatedExam = mapToQuiz(exam);

            for(int i=0; i<exam.getExamQuestions().size(); i++){
                exam.getExamQuestions().get(i).setExamId(updatedExam.getExamId());
            }

            examQuestionRepository.saveAll(exam.getExamQuestions());
            Utility.printDebugLogs("Exam updated successfully: " + updatedExam);

            examResponse = ExamResponse.builder()
                    .examId(updatedExam.getExamId())
                    .examTitle(updatedExam.getExamTitle())
                    .description(updatedExam.getDescription())
                    .totalMarks(updatedExam.getTotalMarks())
                    .visibility(updatedExam.isVisibility())
                    .course(course.get())
                    .examQuestions(exam.getExamQuestions())
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

    public ExamListResponse getExamsByCourseId(Long courseId) {
        Utility.printDebugLogs("Get exams by course ID: " + courseId);
        ExamListResponse examListResponse;

        try {
            List<Exam> examList = examRepository.findAllByCourseId(courseId);

            if (examList.isEmpty()) {
                Utility.printDebugLogs("No exams found for course ID: " + courseId);
                throw new NotFoundException("No exams found for course ID: " + courseId);
            }

            examListResponse = ExamListResponse.builder()
                    .examList(examList)
                    .messageStatus("Success")
                    .build();

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
}
