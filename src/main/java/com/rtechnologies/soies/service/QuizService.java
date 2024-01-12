package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Quiz;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.dto.QuizListResponse;
import com.rtechnologies.soies.model.dto.QuizResponse;
import com.rtechnologies.soies.repository.CourseRepository;
import com.rtechnologies.soies.repository.QuizRepository;
import com.rtechnologies.soies.repository.TeacherRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    public QuizResponse createQuiz(Quiz quiz) {
        Utility.printDebugLogs("Quiz creation request: " + quiz.toString());
        QuizResponse quizResponse;

        try {
            if (quiz == null) {
                Utility.printDebugLogs("Quiz creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for quiz
            Optional<Quiz> existingQuiz = quizRepository.findById(quiz.getAssignmentId());
            if (existingQuiz.isPresent()) {
                throw new IllegalArgumentException("Quiz with ID " + quiz.getAssignmentId() + " already exists");
            }

            //Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(quiz.getTeacherId());
            if(teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + quiz.getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + quiz.getTeacherId());
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(quiz.getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + quiz.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + quiz.getCourseId());
            }

            Quiz createdQuiz = quizRepository.save(quiz);
            Utility.printDebugLogs("Quiz created successfully: " + createdQuiz);

            quizResponse = QuizResponse.builder()
                    .quizId(createdQuiz.getAssignmentId())
                    .quizTitle(createdQuiz.getAssignmentTitle())
                    .description(createdQuiz.getDescription())
                    .totalMarks(createdQuiz.getTotalMarks())
                    .visibility(createdQuiz.isVisibility())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Quiz response: " + quizResponse);
            return quizResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public QuizResponse updateQuiz(Quiz quiz) {
        Utility.printDebugLogs("Quiz update request: " + quiz.toString());
        QuizResponse quizResponse;

        try {
            if (quiz == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for quiz
            Optional<Quiz> existingQuiz = quizRepository.findById(quiz.getAssignmentId());
            if (existingQuiz.isEmpty()) {
                throw new IllegalArgumentException("No Quiz found with ID: " + quiz.getAssignmentId());
            }

            //Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(quiz.getTeacherId());
            if(teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + quiz.getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + quiz.getTeacherId());
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(quiz.getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + quiz.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + quiz.getCourseId());
            }

            Quiz updatedQuiz = quizRepository.save(quiz);
            Utility.printDebugLogs("Quiz updated successfully: " + updatedQuiz);

            quizResponse = QuizResponse.builder()
                    .quizId(updatedQuiz.getAssignmentId())
                    .quizTitle(updatedQuiz.getAssignmentTitle())
                    .description(updatedQuiz.getDescription())
                    .totalMarks(updatedQuiz.getTotalMarks())
                    .visibility(updatedQuiz.isVisibility())
                    .course(course.get())
                    .teacher(teacher.get())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Quiz response: " + quizResponse);
            return quizResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    public QuizResponse deleteQuiz(Long quizId) {
        Utility.printDebugLogs("Quiz deletion request: " + quizId);
        QuizResponse quizResponse;

        try {
            Optional<Quiz> existingQuiz = quizRepository.findById(quizId);

            if (existingQuiz.isEmpty()) {
                throw new IllegalArgumentException("No quiz found with ID: " + quizId);
            }

            quizRepository.deleteById(quizId);
            Utility.printDebugLogs("Quiz deleted successfully: " + existingQuiz.get());

            quizResponse = QuizResponse.builder()
                    .quizId(existingQuiz.get().getAssignmentId())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Quiz response: " + quizResponse);
            return quizResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        }
    }

    //Doesnt requires for now
//    public QuizListResponse getAllQuizzesByTeacherId(Long teacherId) {
//        Utility.printDebugLogs("Get all quizzes by teacher ID: " + teacherId);
//        QuizListResponse quizListResponse;
//
//        try {
//            //Check for teacher
//            Optional<Teacher> teacher = teacherRepository.findById(teacherId);
//            if(teacher.isEmpty()) {
//                Utility.printDebugLogs("No teacher found with ID: " + teacherId);
//                throw new IllegalArgumentException("No teacher found with ID: " + teacherId);
//            }
//
//            List<Quiz> quizzes = quizRepository.findByTeacherId(teacherId);
//            quizListResponse = QuizListResponse.builder()
//                    .quizList(quizzes)
//                    .messageStatus("Success")
//                    .build();
//
//            Utility.printDebugLogs("Quiz list response: " + quizListResponse);
//            return quizListResponse;
//        } catch (IllegalArgumentException e) {
//            Utility.printErrorLogs(e.toString());
//            return QuizListResponse.builder()
//                    .messageStatus(e.toString())
//                    .build();
//        } catch (Exception e) {
//            Utility.printErrorLogs(e.toString());
//            return QuizListResponse.builder()
//                    .messageStatus("Failure")
//                    .build();
//        }
//    }

    public QuizResponse getQuizById(Long quizId) {
        Utility.printDebugLogs("Get quiz by ID: " + quizId);
        QuizResponse quizResponse;

        try {
            Optional<Quiz> optionalQuiz = quizRepository.findById(quizId);

            if (optionalQuiz.isEmpty()) {
                Utility.printDebugLogs("No quiz found with ID: " + quizId);
                throw new IllegalArgumentException("No quiz found with ID: " + quizId);
            }

            //Check for teacher
            Optional<Teacher> teacher = teacherRepository.findById(optionalQuiz.get().getTeacherId());
            if(teacher.isEmpty()) {
                Utility.printDebugLogs("No teacher found with ID: " + optionalQuiz.get().getTeacherId());
                throw new IllegalArgumentException("No teacher found with ID: " + optionalQuiz.get().getTeacherId());
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(optionalQuiz.get().getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + optionalQuiz.get().getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + optionalQuiz.get().getCourseId());
            }

            Quiz quiz = optionalQuiz.get();

            quizResponse = QuizResponse.builder()
                    .quizId(quiz.getAssignmentId())
                    .quizTitle(quiz.getAssignmentTitle())
                    .description(quiz.getDescription())
                    .totalMarks(quiz.getTotalMarks())
                    .visibility(quiz.isVisibility())
                    .messageStatus("Success")
                    .course(course.get())
                    .teacher(teacher.get())
                    .build();

            Utility.printDebugLogs("Quiz response: " + quizResponse);
            return quizResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return QuizResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }

    public QuizListResponse getQuizzesByCourseId(Long courseId) {
        Utility.printDebugLogs("Get quizzes by course ID: " + courseId);
        QuizListResponse quizListResponse;

        try {
            List<Quiz> quizList = quizRepository.findByCourseId(courseId);

            if (quizList.isEmpty()) {
                Utility.printDebugLogs("No quizzes found for course ID: " + courseId);
                throw new IllegalArgumentException("No quizzes found for course ID: " + courseId);
            }

            quizListResponse = QuizListResponse.builder()
                    .quizList(quizList)
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Quiz list response: " + quizListResponse);
            return quizListResponse;
        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            return QuizListResponse.builder()
                    .messageStatus(e.toString())
                    .build();
        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            return QuizListResponse.builder()
                    .messageStatus("Failure")
                    .build();
        }
    }
}
