package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Course;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.AssignmentSubmission;
import com.rtechnologies.soies.model.association.ExamSubmission;
import com.rtechnologies.soies.model.association.OgaSubmission;
import com.rtechnologies.soies.model.association.QuizSubmission;
import com.rtechnologies.soies.model.dto.AcademicRecordResponse;
import com.rtechnologies.soies.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

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

    public AcademicRecordResponse getAcademicRecord(String studentRollNumber, String term, String academicCategory, int page, int size) {
        AcademicRecordResponse academicRecordResponse = new AcademicRecordResponse();
        Optional<Student> student = studentRepository.findByRollNumber(studentRollNumber);

        if (student.isEmpty()) {
            academicRecordResponse.setMessageStatus("Failure");
            throw new NotFoundException("Student not found with roll number: " + studentRollNumber);
        }

        try {
            PageRequest pageable = PageRequest.of(page, size);
            if ("all".equalsIgnoreCase(academicCategory)) {
                // Fetch assignment submissions for the specified term
                Page<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository
                        .findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                academicRecordResponse.setAssignmentSubmissions(assignmentSubmissions.getContent());

                // Fetch exam results for the specified term
                Page<ExamSubmission> examResults = examResultRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                academicRecordResponse.setExamSubmissions(examResults.getContent());

                // Fetch quiz submissions for the specified term
                Page<QuizSubmission> quizSubmissions = quizSubmissionRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                academicRecordResponse.setQuizSubmissions(quizSubmissions.getContent());

                // Fetch OGA results for the specified term
                Page<OgaSubmission> ogaResults = ogaResultRepository.findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                academicRecordResponse.setOgaSubmissions(ogaResults.getContent());
            } else {
                switch (academicCategory.toLowerCase()) {
                    case "assignment":
                        // Fetch assignment submissions for the specified term
                        Page<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository
                                .findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                        academicRecordResponse.setAssignmentSubmissions(assignmentSubmissions.getContent());
                        break;

                    case "exam":
                        // Fetch exam results for the specified term
                        Page<ExamSubmission> examResults = examResultRepository
                                .findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                        academicRecordResponse.setExamSubmissions(examResults.getContent());
                        break;

                    case "quiz":
                        // Fetch quiz submissions for the specified term
                        Page<QuizSubmission> quizSubmissions = quizSubmissionRepository
                                .findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                        academicRecordResponse.setQuizSubmissions(quizSubmissions.getContent());
                        break;

                    case "oga":
                        // Fetch OGA results for the specified term
                        Page<OgaSubmission> ogaResults = ogaResultRepository
                                .findByStudentRollNumberAndTerm(studentRollNumber, term, pageable);
                        academicRecordResponse.setOgaSubmissions(ogaResults.getContent());
                        break;

                    default:
                        academicRecordResponse.setMessageStatus("Failure");
                        throw new IllegalArgumentException("Invalid academic category: " + academicCategory);
                }
            }

            academicRecordResponse.setMessageStatus("Success");

        } catch (Exception e) {
            academicRecordResponse.setMessageStatus("Failure");
            // Handle exception or log error
        }

        return academicRecordResponse;
    }

    public AcademicRecordResponse getClassAcademicRecord(Long courseId, String term, String academicCategory, int page, int size) {
        AcademicRecordResponse academicRecordResponse = new AcademicRecordResponse();

        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            academicRecordResponse.setMessageStatus("Failure");
            throw new NotFoundException("Course not found with courseId: " + courseId);
        }

        try {
            PageRequest pageable = PageRequest.of(page, size);
            if ("all".equalsIgnoreCase(academicCategory)) {
                // Fetch assignment submissions for the specified term and class
                Page<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository
                        .findByCourseIdAndTerm(courseId, term, pageable);
                academicRecordResponse.setAssignmentSubmissions(assignmentSubmissions.getContent());

                // Fetch exam results for the specified term and class
                Page<ExamSubmission> examResults = examResultRepository.findByCourseIdAndTerm(courseId, term, pageable);
                academicRecordResponse.setExamSubmissions(examResults.getContent());

                // Fetch quiz submissions for the specified term and class
                Page<QuizSubmission> quizSubmissions = quizSubmissionRepository.findByCourseIdAndTerm(courseId, term, pageable);
                academicRecordResponse.setQuizSubmissions(quizSubmissions.getContent());

                // Fetch OGA results for the specified term and class
                Page<OgaSubmission> ogaResults = ogaResultRepository.findByCourseIdAndTerm(courseId, term, pageable);
                academicRecordResponse.setOgaSubmissions(ogaResults.getContent());
            } else {
                switch (academicCategory.toLowerCase()) {
                    case "assignment":
                        // Fetch assignment submissions for the specified term and class
                        Page<AssignmentSubmission> assignmentSubmissions = assignmentSubmissionRepository
                                .findByCourseIdAndTerm(courseId, term, pageable);
                        academicRecordResponse.setAssignmentSubmissions(assignmentSubmissions.getContent());
                        break;

                    case "exam":
                        // Fetch exam results for the specified term and class
                        Page<ExamSubmission> examResults = examResultRepository
                                .findByCourseIdAndTerm(courseId, term, pageable);
                        academicRecordResponse.setExamSubmissions(examResults.getContent());
                        break;

                    case "quiz":
                        // Fetch quiz submissions for the specified term and class
                        Page<QuizSubmission> quizSubmissions = quizSubmissionRepository
                                .findByCourseIdAndTerm(courseId, term, pageable);
                        academicRecordResponse.setQuizSubmissions(quizSubmissions.getContent());
                        break;

                    case "oga":
                        // Fetch OGA results for the specified term and class
                        Page<OgaSubmission> ogaResults = ogaResultRepository
                                .findByCourseIdAndTerm(courseId, term, pageable);
                        academicRecordResponse.setOgaSubmissions(ogaResults.getContent());
                        break;

                    default:
                        academicRecordResponse.setMessageStatus("Failure");
                        throw new IllegalArgumentException("Invalid academic category: " + academicCategory);
                }
            }

            academicRecordResponse.setMessageStatus("Success");

        } catch (Exception e) {
            academicRecordResponse.setMessageStatus("Failure");
            // Handle exception or log error
        }

        return academicRecordResponse;
    }

}
