package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.*;
import com.rtechnologies.soies.model.association.AssignmentSubmission;
import com.rtechnologies.soies.model.association.ExamSubmission;
import com.rtechnologies.soies.model.association.OgaSubmission;
import com.rtechnologies.soies.model.association.QuizSubmission;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.*;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AcademicRecordService {

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Autowired
    private ExamSubmissionRepository examResultRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private OgaSubmissionRepository ogaResultRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private OgaRepository ogaRepository;

    @Autowired
    private ExamRepository examRepository;


    public AcademicRecordResponse getAcademicRecord(String studentRollNumber, String term, String academicCategory, int page, int size) {
        AcademicRecordResponse academicRecordResponse = new AcademicRecordResponse();
        Optional<Student> student = studentRepository.findByRollNumber(studentRollNumber);

        if (student.isEmpty()) {
            academicRecordResponse.setMessageStatus("Failure");
            throw new NotFoundException("Student not found with roll number: " + studentRollNumber);
        }

        try {
            PageRequest pageable = PageRequest.of(page, size);
            switch (academicCategory.toLowerCase()) {
                case "all":
                    // Fetch assignment submissions
                    Page<AssignmentSubmission> assignmentSubmissions;
                    if ("all".equalsIgnoreCase(term)) {
                        assignmentSubmissions = assignmentSubmissionRepository.findByStudentRollNumber(studentRollNumber, pageable);
                    } else {
                        assignmentSubmissions = assignmentSubmissionRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                    }
                    academicRecordResponse.setAssignmentSubmissions(
                            assignmentSubmissions.getContent().stream()
                                    .map(submission -> {
                                        String title = getAssignmentTitleById(submission.getAssignmentId());
                                        return AssignmentSubmissionDTO.builder()
                                                .submissionId(submission.getSubmissionId())
                                                .assignmentId(submission.getAssignmentId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .courseId(submission.getCourseId())
                                                .studentName(submission.getStudentName())
                                                .submissionDate(submission.getSubmissionDate())
                                                .submittedFileURL(submission.getSubmittedFileURL())
                                                .comments(submission.getComments())
                                                .obtainedMarks(submission.getObtainedMarks())
                                                .obtainedGrade(submission.getObtainedGrade())
                                                .dueDate(submission.getDueDate())
                                                .term(submission.getTerm())
                                                .totalMarks(submission.getTotalMarks())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));

                    // Fetch exam submissions
                    Page<ExamSubmission> examResults;
                    if ("all".equalsIgnoreCase(term)) {
                        examResults = examResultRepository.findByStudentRollNumber(studentRollNumber, pageable);
                    } else {
                        examResults = examResultRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                    }
                    academicRecordResponse.setExamSubmissions(
                            examResults.getContent().stream()
                                    .map(submission -> {
                                        String title = getExamTitleById(submission.getExamId());
                                        return ExamSubmissionDTO.builder()
                                                .id(submission.getId())
                                                .examId(submission.getExamId())
                                                .courseId(submission.getCourseId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .totalMarks(submission.getTotalMarks())
                                                .gainedMarks(submission.getGainedMarks())
                                                .percentage(submission.getPercentage())
                                                .date(submission.getDate())
                                                .term(submission.getTerm())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));

                    // Fetch quiz submissions
                    Page<QuizSubmission> quizSubmissions;
                    if ("all".equalsIgnoreCase(term)) {
                        quizSubmissions = quizSubmissionRepository.findByStudentRollNumber(studentRollNumber, pageable);
                    } else {
                        quizSubmissions = quizSubmissionRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                    }
                    academicRecordResponse.setQuizSubmissions(
                            quizSubmissions.getContent().stream()
                                    .map(submission -> {
                                        String title = getQuizTitleById(submission.getQuizId());
                                        return QuizSubmissionDTO.builder()
                                                .id(submission.getId())
                                                .quizId(submission.getQuizId())
                                                .courseId(submission.getCourseId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .totalMarks(submission.getTotalMarks())
                                                .gainedMarks(submission.getGainedMarks())
                                                .percentage(submission.getPercentage())
                                                .date(submission.getDate())
                                                .term(submission.getTerm())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));

                    // Fetch OGA submissions
                    Page<OgaSubmission> ogaResults;
                    if ("all".equalsIgnoreCase(term)) {
                        ogaResults = ogaResultRepository.findByStudentRollNumber(studentRollNumber, pageable);
                    } else {
                        ogaResults = ogaResultRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                    }
                    academicRecordResponse.setOgaSubmissions(
                            ogaResults.getContent().stream()
                                    .map(submission -> {
                                        String title = getOgaTitleById(submission.getOgaId());
                                        return OgaSubmissionDTO.builder()
                                                .id(submission.getId())
                                                .ogaId(submission.getOgaId())
                                                .courseId(submission.getCourseId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .totalMarks(submission.getTotalMarks())
                                                .gainedMarks(submission.getGainedMarks())
                                                .percentage(submission.getPercentage())
                                                .date(submission.getDate())
                                                .term(submission.getTerm())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));
                    break;

                case "assignment":
                    Page<AssignmentSubmission> assignments = term.equalsIgnoreCase("all")
                            ? assignmentSubmissionRepository.findByStudentRollNumber(studentRollNumber, pageable)
                            : assignmentSubmissionRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);

                    academicRecordResponse.setAssignmentSubmissions(
                            assignments.getContent().stream()
                                    .map(submission -> {
                                        String title = getAssignmentTitleById(submission.getAssignmentId());
                                        return AssignmentSubmissionDTO.builder()
                                                .submissionId(submission.getSubmissionId())
                                                .assignmentId(submission.getAssignmentId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .courseId(submission.getCourseId())
                                                .studentName(submission.getStudentName())
                                                .submissionDate(submission.getSubmissionDate())
                                                .submittedFileURL(submission.getSubmittedFileURL())
                                                .comments(submission.getComments())
                                                .obtainedMarks(submission.getObtainedMarks())
                                                .obtainedGrade(submission.getObtainedGrade())
                                                .dueDate(submission.getDueDate())
                                                .term(submission.getTerm())
                                                .totalMarks(submission.getTotalMarks())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));
                    break;
                case "exam":
                    Page<ExamSubmission> exams = term.equalsIgnoreCase("all")
                            ? examResultRepository.findByStudentRollNumber(studentRollNumber, pageable)
                            : examResultRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);

                    academicRecordResponse.setExamSubmissions(
                            exams.getContent().stream()
                                    .map(submission -> {
                                        String title = getExamTitleById(submission.getExamId());
                                        return ExamSubmissionDTO.builder()
                                                .id(submission.getId())
                                                .examId(submission.getExamId())
                                                .courseId(submission.getCourseId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .totalMarks(submission.getTotalMarks())
                                                .gainedMarks(submission.getGainedMarks())
                                                .percentage(submission.getPercentage())
                                                .date(submission.getDate())
                                                .term(submission.getTerm())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));
                    break;

                case "quiz":
                    Page<QuizSubmission> quizzes = term.equalsIgnoreCase("all")
                            ? quizSubmissionRepository.findByStudentRollNumber(studentRollNumber, pageable)
                            : quizSubmissionRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);

                    academicRecordResponse.setQuizSubmissions(
                            quizzes.getContent().stream()
                                    .map(submission -> {
                                        String title = getQuizTitleById(submission.getQuizId());
                                        return QuizSubmissionDTO.builder()
                                                .id(submission.getId())
                                                .quizId(submission.getQuizId())
                                                .courseId(submission.getCourseId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .totalMarks(submission.getTotalMarks())
                                                .gainedMarks(submission.getGainedMarks())
                                                .percentage(submission.getPercentage())
                                                .date(submission.getDate())
                                                .term(submission.getTerm())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));
                    break;
                case "oga":
                    Page<OgaSubmission> ogas = term.equalsIgnoreCase("all")
                            ? ogaResultRepository.findByStudentRollNumber(studentRollNumber, pageable)
                            : ogaResultRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);

                    academicRecordResponse.setOgaSubmissions(
                            ogas.getContent().stream()
                                    .map(submission -> {
                                        String title = getOgaTitleById(submission.getOgaId());
                                        return OgaSubmissionDTO.builder()
                                                .id(submission.getId())
                                                .ogaId(submission.getOgaId())
                                                .courseId(submission.getCourseId())
                                                .studentRollNumber(submission.getStudentRollNumber())
                                                .totalMarks(submission.getTotalMarks())
                                                .gainedMarks(submission.getGainedMarks())
                                                .percentage(submission.getPercentage())
                                                .date(submission.getDate())
                                                .term(submission.getTerm())
                                                .title(title)
                                                .build();
                                    })
                                    .collect(Collectors.toList()));
                    break;

                default:
                    academicRecordResponse.setMessageStatus("Failure");
                    throw new IllegalArgumentException("Invalid academic category: " + academicCategory);
            }

            academicRecordResponse.setMessageStatus("Success");

        } catch (Exception e) {
            academicRecordResponse.setMessageStatus("Failure");
            // Optionally, log the exception for debugging
            // log.error("Error retrieving academic record", e);
        }

        return academicRecordResponse;
    }

    private String getAssignmentTitleById(Long assignmentId) {
        // Fetch the title from the assignment repository or service
        return assignmentRepository.findById(assignmentId)
                .map(Assignment::getAssignmentTitle)
                .orElse("Unknown Title");
    }

    private String getExamTitleById(Long examId) {
        // Fetch the title from the exam repository or service
        return examRepository.findById(examId)
                .map(Exam::getExamTitle)
                .orElse("Unknown Title");
    }

    private String getQuizTitleById(Long quizId) {
        // Fetch the title from the quiz repository or service
        return quizRepository.findById(quizId)
                .map(Quiz::getQuizTitle)
                .orElse("Unknown Title");
    }

    private String getOgaTitleById(Long ogaId) {
        // Fetch the title from the OGA repository or service
        return ogaRepository.findById(ogaId)
                .map(Oga::getOgaTitle)
                .orElse("Unknown Title");
    }

//    public AcademicRecordResponse getClassAcademicRecord(Long courseId, String term, String academicCategory, int page, int size) {
//        AcademicRecordResponse academicRecordResponse = new AcademicRecordResponse();
//
//        Optional<Course> course = courseRepository.findById(courseId);
//        if (course.isEmpty()) {
//            academicRecordResponse.setMessageStatus("Failure");
//            throw new NotFoundException("Course not found with courseId: " + courseId);
//        }
//
//        try {
//            PageRequest pageable = PageRequest.of(page, size);
//            if ("all".equalsIgnoreCase(academicCategory)) {
//                // Fetch assignment submissions for the specified term and class
//                Page<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository
//                        .findByCourseIdAndTerm(courseId, term, pageable);
//                academicRecordResponse.setAssignmentSubmissions(assignmentSubmissions.getContent());
//
//                // Fetch exam results for the specified term and class
//                Page<ExamSubmission> examResults = examResultRepository.findByCourseIdAndTerm(courseId, term, pageable);
//                academicRecordResponse.setExamSubmissions(examResults.getContent());
//
//                // Fetch quiz submissions for the specified term and class
//                Page<QuizSubmission> quizSubmissions = quizSubmissionRepository.findByCourseIdAndTerm(courseId, term, pageable);
//                academicRecordResponse.setQuizSubmissions(quizSubmissions.getContent());
//
//                // Fetch OGA results for the specified term and class
//                Page<OgaSubmission> ogaResults = ogaResultRepository.findByCourseIdAndTerm(courseId, term, pageable);
//                academicRecordResponse.setOgaSubmissions(ogaResults.getContent());
//            } else {
//                switch (academicCategory.toLowerCase()) {
//                    case "assignment":
//                        // Fetch assignment submissions for the specified term and class
//                        Page<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository
//                                .findByCourseIdAndTerm(courseId, term, pageable);
//                        academicRecordResponse.setAssignmentSubmissions(assignmentSubmissions.getContent());
//                        break;
//
//                    case "exam":
//                        // Fetch exam results for the specified term and class
//                        Page<ExamSubmission> examResults = examResultRepository
//                                .findByCourseIdAndTerm(courseId, term, pageable);
//                        academicRecordResponse.setExamSubmissions(examResults.getContent());
//                        break;
//
//                    case "quiz":
//                        // Fetch quiz submissions for the specified term and class
//                        Page<QuizSubmission> quizSubmissions = quizSubmissionRepository
//                                .findByCourseIdAndTerm(courseId, term, pageable);
//                        academicRecordResponse.setQuizSubmissions(quizSubmissions.getContent());
//                        break;
//
//                    case "oga":
//                        // Fetch OGA results for the specified term and class
//                        Page<OgaSubmission> ogaResults = ogaResultRepository
//                                .findByCourseIdAndTerm(courseId, term, pageable);
//                        academicRecordResponse.setOgaSubmissions(ogaResults.getContent());
//                        break;
//
//                    default:
//                        academicRecordResponse.setMessageStatus("Failure");
//                        throw new IllegalArgumentException("Invalid academic category: " + academicCategory);
//                }
//            }
//
//            academicRecordResponse.setMessageStatus("Success");
//
//        } catch (Exception e) {
//            academicRecordResponse.setMessageStatus("Failure");
//            // Handle exception or log error
//        }
//
//        return academicRecordResponse;
//    }

}
