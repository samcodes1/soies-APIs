package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Exam;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.ExamListResponse;
import com.rtechnologies.soies.model.dto.ExamResponse;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.ExamRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ExamResponse createExam(Exam exam) {
        Utility.printDebugLogs("Exam creation request: " + exam.toString());
        ExamResponse examResponse;

        try {
            if (exam == null) {
                Utility.printDebugLogs("Exam creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for exam
            Optional<Exam> existingExam = examRepository.findById(exam.getAssignmentId());
            if (existingExam.isPresent()) {
                throw new IllegalArgumentException("Exam with ID " + exam.getAssignmentId() + " already exists");
            }

            // Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(exam.getTeacherId());
            if (teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + exam.getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + exam.getTeacherId());
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(exam.getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + exam.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + exam.getCourseId());
            }

            Exam createdExam = examRepository.save(exam);
            Utility.printDebugLogs("Exam created successfully: " + createdExam);

            examResponse = ExamResponse.builder()
                    .assignmentId(createdExam.getAssignmentId())
                    .examTitle(createdExam.getAssignmentTitle())
                    .description(createdExam.getDescription())
                    .totalMarks(createdExam.getTotalMarks())
                    .visibility(createdExam.isVisibility())
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

    public ExamResponse updateExam(Exam exam) {
        Utility.printDebugLogs("Exam update request: " + exam.toString());
        ExamResponse examResponse;

        try {
            if (exam == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for exam
            Optional<Exam> existingExam = examRepository.findById(exam.getAssignmentId());
            if (existingExam.isEmpty()) {
                throw new IllegalArgumentException("No Exam found with ID: " + exam.getAssignmentId());
            }

            // Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(exam.getTeacherId());
            if (teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + exam.getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + exam.getTeacherId());
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(exam.getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + exam.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + exam.getCourseId());
            }

            Exam updatedExam = examRepository.save(exam);
            Utility.printDebugLogs("Exam updated successfully: " + updatedExam);

            examResponse = ExamResponse.builder()
                    .assignmentId(updatedExam.getAssignmentId())
                    .examTitle(updatedExam.getAssignmentTitle())
                    .description(updatedExam.getDescription())
                    .totalMarks(updatedExam.getTotalMarks())
                    .visibility(updatedExam.isVisibility())
                    .course(course.get())
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
                throw new IllegalArgumentException("No exam found with ID: " + examId);
            }

            examRepository.deleteById(examId);
            Utility.printDebugLogs("Exam deleted successfully: " + existingExam.get());

            examResponse = ExamResponse.builder()
                    .assignmentId(existingExam.get().getAssignmentId())
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
                throw new IllegalArgumentException("No exams found for course ID: " + courseId);
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
                throw new IllegalArgumentException("No exam found with ID: " + examId);
            }

            // Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(optionalExam.get().getTeacherId());
            if (teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + optionalExam.get().getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + optionalExam.get().getTeacherId());
            }

            // Check for course
            Optional<Course> course = courseRepository.findById(optionalExam.get().getCourseId());
            if (course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + optionalExam.get().getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + optionalExam.get().getCourseId());
            }

            Exam exam = optionalExam.get();

            examResponse = ExamResponse.builder()
                    .assignmentId(exam.getAssignmentId())
                    .examTitle(exam.getAssignmentTitle())
                    .description(exam.getDescription())
                    .totalMarks(exam.getTotalMarks())
                    .visibility(exam.isVisibility())
                    .course(course.get())
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
