package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Quiz;
import com.rtechnologies.soies.model.QuizQuestion;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.association.QuizStudentAnswer;
import com.rtechnologies.soies.model.association.QuizSubmission;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.*;
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

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private QuizStudentAnswerRepository quizStudentAnswerRepository;
    public QuizResponse createQuiz(CreateQuizRequest quiz) {
        Utility.printDebugLogs("Quiz creation request: " + quiz.toString());
        QuizResponse quizResponse;

        try {
            if (quiz == null) {
                Utility.printDebugLogs("Quiz creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(quiz.getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + quiz.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + quiz.getCourseId());
            }

            Quiz createdQuiz = mapToQuiz(quiz);
            Utility.printDebugLogs("Quiz created successfully: " + createdQuiz);

            for(int i=0; i<quiz.getQuizQuestions().size(); i++){
                quiz.getQuizQuestions().get(i).setQuizId(createdQuiz.getQuizId());
            }

            quizQuestionRepository.saveAll(quiz.getQuizQuestions());

            quizResponse = QuizResponse.builder()
                    .quizId(createdQuiz.getQuizId())
                    .quizTitle(createdQuiz.getQuizTitle())
                    .description(createdQuiz.getDescription())
                    .totalMarks(createdQuiz.getTotalMarks())
                    .visibility(createdQuiz.isVisibility())
                    .quizQuestions(quiz.getQuizQuestions())
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

    public Quiz mapToQuiz(CreateQuizRequest createQuizRequest) {
        return quizRepository.save(Quiz.builder()
                .courseId(createQuizRequest.getCourseId())
                .quizTitle(createQuizRequest.getQuizTitle())
                .description(createQuizRequest.getDescription())
                .dueDate(createQuizRequest.getDueDate())
                .time(createQuizRequest.getTime())
                .totalMarks(createQuizRequest.getTotalMarks())
                .visibility(createQuizRequest.isVisibility())
                .build());
    }

    public Quiz mapToQuiz(QuizRequest createQuizRequest) {
        return quizRepository.save(Quiz.builder()
                .courseId(createQuizRequest.getCourseId())
                .quizTitle(createQuizRequest.getQuizTitle())
                .description(createQuizRequest.getDescription())
                .dueDate(createQuizRequest.getDueDate())
                .time(createQuizRequest.getTime())
                .totalMarks(createQuizRequest.getTotalMarks())
                .visibility(createQuizRequest.isVisibility())
                .build());
    }

    public QuizResponse updateQuiz(QuizRequest quiz) {
        Utility.printDebugLogs("Quiz update request: " + quiz.toString());
        QuizResponse quizResponse;

        try {
            if (quiz == null) {
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for quiz
            Optional<Quiz> existingQuiz = quizRepository.findById(quiz.getQuizId());
            if (existingQuiz.isEmpty()) {
                throw new IllegalArgumentException("No Quiz found with ID: " + quiz.getQuizId());
            }

            //Check for course
            Optional<Course> course = courseRepository.findById(quiz.getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + quiz.getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + quiz.getCourseId());
            }

            Quiz createdQuiz = mapToQuiz(quiz);
            Utility.printDebugLogs("Quiz created successfully: " + createdQuiz);

            for(int i=0; i<quiz.getQuizQuestions().size(); i++){
                quiz.getQuizQuestions().get(i).setQuizId(createdQuiz.getQuizId());
            }

            quizQuestionRepository.saveAll(quiz.getQuizQuestions());

            quizResponse = QuizResponse.builder()
                    .quizId(createdQuiz.getQuizId())
                    .quizTitle(createdQuiz.getQuizTitle())
                    .description(createdQuiz.getDescription())
                    .totalMarks(createdQuiz.getTotalMarks())
                    .visibility(createdQuiz.isVisibility())
                    .quizQuestions(quiz.getQuizQuestions())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Quiz update response: " + quizResponse);
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
                    .quizId(existingQuiz.get().getQuizId())
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

            //Check for course
            Optional<Course> course = courseRepository.findById(optionalQuiz.get().getCourseId());
            if(course.isEmpty()) {
                Utility.printDebugLogs("No course found with ID: " + optionalQuiz.get().getCourseId());
                throw new IllegalArgumentException("No course found with ID: " + optionalQuiz.get().getCourseId());
            }

            Quiz quiz = optionalQuiz.get();
            List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizId(quizId);

            quizResponse = QuizResponse.builder()
                    .quizId(quiz.getQuizId())
                    .quizTitle(quiz.getQuizTitle())
                    .description(quiz.getDescription())
                    .totalMarks(quiz.getTotalMarks())
                    .visibility(quiz.isVisibility())
                    .quizQuestions(quizQuestions)
                    .time(quiz.getTime())
                    .messageStatus("Success")
                    .course(course.get())
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


    //Quiz submission APIs
    public String submitQuiz(QuizSubmissionRequest quizSubmissionRequest){
        QuizSubmission quizSubmission = new QuizSubmission();
        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizId(quizSubmissionRequest.getQuizId());

        if(quizQuestions.isEmpty()) {
            throw new IllegalArgumentException("No quiz found with ID: " + quizSubmissionRequest.getCourseId());
        }
        quizSubmission = mapToQuizSubmission(quizSubmissionRequest);
        quizSubmissionRepository.save(quizSubmission);


        int totalMarks = quizSubmission.getTotalMarks();
        int perQuestionMark = totalMarks/quizQuestions.size();
        int gainedMarks = 0;

        //Save answers to the DB
        for(int i=0; i<quizSubmissionRequest.getQuizQuestionList().size(); i++){
            boolean isCorrect = false;

            if(quizSubmissionRequest.getQuizQuestionList().
                    get(i).getAnswer().equals(quizQuestions.get(i).getAnswer())) {
                gainedMarks+=perQuestionMark;
                isCorrect=true;
            }

            quizStudentAnswerRepository.save(QuizStudentAnswer.builder().
                    quizSubmissionId(quizSubmission.getQuizId())
                    .questionId(quizQuestions.get(i).getId())
                    .answer(quizSubmissionRequest.getQuizQuestionList().
                            get(i).getAnswer())
                    .isCorrect(isCorrect)
                    . build());
        }

        double percentage = (double) gainedMarks/totalMarks * 100;
        quizSubmission.setGainedMarks(gainedMarks);
        quizSubmission.setPercentage(percentage);

        quizSubmissionRepository.save(quizSubmission);

        return "Quiz submitted successfully";
    }

    public QuizSubmissionListResponse getAllQuizSubmission(Long quizId){
        List<QuizSubmission> submittedQuizzes = quizSubmissionRepository.findByQuizId(quizId);
        QuizSubmissionListResponse quizSubmissionResponse = new QuizSubmissionListResponse();
        if(submittedQuizzes.isEmpty()) {
            throw new IllegalArgumentException("No quiz found with ID: " + quizId);
        }

        quizSubmissionResponse.setQuizSubmissionList(submittedQuizzes);
        quizSubmissionResponse.setMessageStatus("Success");

        return quizSubmissionResponse;
    }

    //To be implemented during admin side
//    public QuizSubmissionResponse getQuizSubmissionByStudentId(){
//
//    }

    public static QuizSubmission mapToQuizSubmission(QuizSubmissionRequest submissionRequest) {
        return QuizSubmission.builder()
                .quizId(submissionRequest.getQuizId())
                .courseId(submissionRequest.getCourseId())
                .studentRollNumber(submissionRequest.getStudentRollNumber())
                .totalMarks(submissionRequest.getTotalMarks())
                .gainedMarks(0)
                .build();
    }
}
