package com.rtechnologies.soies.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rtechnologies.soies.model.Assignment;
import com.rtechnologies.soies.model.Lecture;
import com.rtechnologies.soies.model.LectureUpdateModel;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.association.LectureReport;
import com.rtechnologies.soies.model.dto.*;
import com.rtechnologies.soies.repository.LectureReportRepository;
import com.rtechnologies.soies.repository.LectureRepository;
import com.rtechnologies.soies.repository.StudentRepository;
import com.rtechnologies.soies.utilities.Utility;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LectureService {
    @Autowired
    private LectureRepository lectureRepository;
    @Autowired
    private LectureReportRepository lectureReportRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private Cloudinary cloudinary;

    public LectureResponse addLecture(CreateLectureRequest lecture) {
        Utility.printDebugLogs("Lecture creation request: " + lecture.toString());
        LectureResponse lectureResponse = new LectureResponse();

        System.out.println("REQUEST OBJ:>>>>> " + lecture.toString());
        try {
            // Validate lecture
            if (lecture == null) {
                Utility.printErrorLogs("Lecture creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            System.out.println("Done 1");
            String fileName = "";
            fileName = lecture.getLectureTitle().toLowerCase() + "-" + lecture.getCourseId();
            Lecture createdLecture = new Lecture();
            try {
                //For file
                String folder = "uploaded-lecture";
                String publicId = folder + "/" + fileName;
                Map<?, ?> data = cloudinary.uploader().upload(lecture.getFile().getBytes(), ObjectUtils.asMap("public_id", publicId));
                String url = data.get("url").toString();

                System.out.println("Done 2");
                String videoUrl = "";
                //For video
                if (lecture.getVideoURL() != null) {
                    data = null;
                    fileName = lecture.getLectureTitle().toLowerCase() + "-" + lecture.getCourseId() + "-" + "video";
                    publicId = folder + "/" + fileName;
                    data = cloudinary.uploader().upload(lecture.getVideoURL().getBytes(), ObjectUtils.asMap("resource_type", "video", "public_id", publicId));
                    videoUrl = data.get("url").toString();
                }
                System.out.println("Done 3");
                Lecture lecture1 = new Lecture();

                lecture1.setLectureTitle(lecture.getLectureTitle());
                lecture1.setPowerPointURL(url);
                lecture1.setDescription(lecture.getDescription());
                lecture1.setVisible(true);
                lecture1.setCourseId(lecture.getCourseId());
                lecture1.setTotalViews(0);
                lecture1.setVideoURL(videoUrl);
                lecture1.setPublishDate(lecture.getPublishDate());
                createdLecture = lectureRepository.save(lecture1);

            } catch (IOException ioException) {
                throw new RuntimeException("File uploading failed");
            }

            Utility.printDebugLogs("Lecture created successfully: " + createdLecture);

            lectureResponse = LectureResponse.builder()
                    .lectureId(createdLecture.getLectureId())
                    .courseId(createdLecture.getCourseId())
                    .lectureTitle(createdLecture.getLectureTitle())
                    .description(createdLecture.getDescription())
                    .videoURL(createdLecture.getVideoURL())
                    .powerPointURL(createdLecture.getPowerPointURL())
                    .totalViews(createdLecture.getTotalViews())
                    .isVisible(createdLecture.isVisible())
                    .publishDate(createdLecture.getPublishDate())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Lecture response: " + lectureResponse);
            return lectureResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs(e.toString());
            lectureResponse.setMessageStatus(e.toString());
            return lectureResponse;

        } catch (Exception e) {
            Utility.printErrorLogs(e.toString());
            lectureResponse.setMessageStatus("Failure");
            return lectureResponse;
        }
    }

    public LectureResponse updateLecture(LectureUpdateModel lecture) throws JsonProcessingException {
        System.out.println("Lecture update request: " + lecture.getLectureId() + lecture.getVideoURL());
        LectureResponse lectureResponse = new LectureResponse();
        String fileName = lecture.getLectureTitle().toLowerCase() + "-" + lecture.getCourseId();

        try {
            // Validate lecture
            if (lecture == null) {
                Utility.printErrorLogs("Lecture update request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check if the lecture exists
            Optional<Lecture> optionalLecture = lectureRepository.findById(lecture.getLectureId());
            if (!optionalLecture.isPresent()) {
                Utility.printErrorLogs("No record found for Lecture ID: " + lecture.getLectureId());
                throw new NotFoundException("No record found for Lecture ID: " + lecture.getLectureId());
            }

            Lecture existingLecture = optionalLecture.get();

            // Update PowerPoint URL if provided
            String powerPointUrl = existingLecture.getPowerPointURL();  // Default to existing value
            if (lecture.getPowerPointURL() != null && !lecture.getPowerPointURL().isEmpty()) {
                String folder = "uploaded-lecture";
                String publicId = folder + "/" + fileName;
                Map<?, ?> data = cloudinary.uploader().upload(lecture.getPowerPointURL().getBytes(), ObjectUtils.asMap("public_id", publicId));
                powerPointUrl = data.get("url").toString();
            }

            // Update video URL if provided
            String videoUrl = existingLecture.getVideoURL();  // Default to existing value
            if (lecture.getVideoURL() != null && !lecture.getVideoURL().isEmpty()) {
                fileName = lecture.getLectureTitle().toLowerCase() + "-" + lecture.getCourseId() + "-" + "video";
                String folder = "uploaded-lecture";
                String publicId = folder + "/" + fileName;
                Map<?, ?> data = cloudinary.uploader().upload(lecture.getVideoURL().getBytes(), ObjectUtils.asMap("resource_type", "video", "public_id", publicId));
                videoUrl = data.get("url").toString();
            }

            // Update the lecture entity with provided fields
            if (lecture.getLectureTitle() != null) {
                existingLecture.setLectureTitle(lecture.getLectureTitle());
            }
            if (lecture.getDescription() != null) {
                existingLecture.setDescription(lecture.getDescription());
            }
            if (lecture.getTotalViews() != 0) { // Assuming 0 is not a valid update value
                existingLecture.setTotalViews(lecture.getTotalViews());
            }
            if (lecture.getPublishDate() != null) {
                existingLecture.setPublishDate(lecture.getPublishDate());
            }
            existingLecture.setVisible(lecture.isVisible());
            existingLecture.setVideoURL(videoUrl);
            existingLecture.setPowerPointURL(powerPointUrl);

            Lecture updatedLecture = lectureRepository.save(existingLecture);
            Utility.printDebugLogs("Lecture updated successfully: " + updatedLecture);

            lectureResponse = LectureResponse.builder()
                    .lectureId(updatedLecture.getLectureId())
                    .courseId(updatedLecture.getCourseId())
                    .lectureTitle(updatedLecture.getLectureTitle())
                    .description(updatedLecture.getDescription())
                    .videoURL(updatedLecture.getVideoURL())
                    .powerPointURL(updatedLecture.getPowerPointURL())
                    .totalViews(updatedLecture.getTotalViews())
                    .isVisible(updatedLecture.isVisible())
                    .publishDate(updatedLecture.getPublishDate())
                    .messageStatus("Success")
                    .build();

            Utility.printDebugLogs("Lecture response: " + lectureResponse);
            return lectureResponse;

        } catch (NotFoundException e) {
            Utility.printErrorLogs("Error updating lecture: " + e.getMessage());
            lectureResponse.setMessageStatus(e.getMessage());
            return lectureResponse;

        } catch (IllegalArgumentException e) {
            Utility.printErrorLogs("Error updating lecture: " + e.getMessage());
            lectureResponse.setMessageStatus(e.getMessage());
            return lectureResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error updating lecture: " + e.getMessage());
            lectureResponse.setMessageStatus("Failure");
            return lectureResponse;
        }
    }

    public LectureResponse deleteLecture(long lectureId) {
        Utility.printDebugLogs("Lecture deletion request for ID: " + lectureId);
        LectureResponse lectureResponse = new LectureResponse();

        try {
            // Validate lectureId
            if (lectureId <= 0) {
                Utility.printErrorLogs("Invalid lectureId for deletion");
                lectureResponse.setMessageStatus("Failure");
                return lectureResponse;
            }

            // Delete the lecture
            lectureRepository.deleteById(lectureId);

            Utility.printDebugLogs("Lecture deleted successfully. ID: " + lectureId);
            lectureResponse.setMessageStatus("Success");
            return lectureResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Unexpected error deleting lecture: " + e.getMessage());
            lectureResponse.setMessageStatus("Failure");
            return lectureResponse;
        }
    }

    public LectureListResponse getLecturesByCourseId(long courseId) {
        Utility.printDebugLogs("Get all lectures by course ID request: " + courseId);
        LectureListResponse lectureListResponse = new LectureListResponse();

        try {
            // Validate courseId
            if (courseId <= 0) {
                Utility.printErrorLogs("Invalid courseId for fetching lectures");
                lectureListResponse.setMessageStatus("Failure");
                return lectureListResponse;
            }

            // Fetch lectures by courseId
            List<Lecture> lectureList = lectureRepository.findAllByCourseId(courseId);
            if (lectureList.size() <= 0) {
                Utility.printDebugLogs("No record found for courses");
                lectureListResponse.setMessageStatus("Success");
                return lectureListResponse;
            }

            Utility.printDebugLogs("Fetched " + lectureList.size() + " lectures by course ID: " + courseId);
            lectureListResponse.setLectureList(lectureList);
            lectureListResponse.setMessageStatus("Success");

            Utility.printDebugLogs("Lecture List Response: " + lectureListResponse);
            return lectureListResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error fetching lectures by course ID: " + e.getMessage());
            lectureListResponse.setMessageStatus("Failure");
            return lectureListResponse;
        }
    }

    public LectureResponse getLectureById(long lectureId) {
        Utility.printDebugLogs("Get lecture by ID request: " + lectureId);
        LectureResponse lectureResponse = new LectureResponse();

        try {
            // Validate lectureId
            if (lectureId <= 0) {
                Utility.printErrorLogs("Invalid lectureId for fetching lecture");
                lectureResponse.setMessageStatus("Failure");
                return lectureResponse;
            }

            // Fetch lecture by lectureId
            Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
            if (optionalLecture.isPresent()) {
                Lecture fetchedLecture = optionalLecture.get();
                Utility.printDebugLogs("Lecture found for ID: " + optionalLecture.get().getLectureId());
                lectureResponse = LectureResponse.builder()
                        .lectureId(fetchedLecture.getLectureId())
                        .courseId(fetchedLecture.getCourseId())
                        .lectureTitle(fetchedLecture.getLectureTitle())
                        .description(fetchedLecture.getDescription())
                        .videoURL(fetchedLecture.getVideoURL())
                        .powerPointURL(fetchedLecture.getPowerPointURL())
                        .totalViews(fetchedLecture.getTotalViews())
                        .isVisible(fetchedLecture.isVisible()).publishDate(fetchedLecture.getPublishDate())
                        .messageStatus("Success")
                        .build();

            } else {
                Utility.printErrorLogs("No record found for Lecture ID: " + lectureId);
                lectureResponse.setMessageStatus("Failure");
            }

            Utility.printDebugLogs("Lecture response: " + lectureResponse);
            return lectureResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error fetching lecture by ID: " + e.getMessage());
            lectureResponse.setMessageStatus("Failure");
            return lectureResponse;
        }
    }

    public LectureResponse getLectureById(long lectureId, String studentRollNumber) {
        Utility.printDebugLogs("Get lecture by ID request: " + lectureId);
        LectureResponse lectureResponse = new LectureResponse();

        try {
            // Validate lectureId
            if (lectureId <= 0) {
                Utility.printErrorLogs("Invalid lectureId for fetching lecture");
                lectureResponse.setMessageStatus("Failure");
                return lectureResponse;
            }

            // Fetch lecture by lectureId
            Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
            if (optionalLecture.isPresent()) {
                Lecture fetchedLecture = optionalLecture.get();
                Utility.printDebugLogs("Lecture found for ID: " + optionalLecture.get().getLectureId());
                lectureResponse = LectureResponse.builder()
                        .lectureId(fetchedLecture.getLectureId())
                        .courseId(fetchedLecture.getCourseId())
                        .lectureTitle(fetchedLecture.getLectureTitle())
                        .description(fetchedLecture.getDescription())
                        .videoURL(fetchedLecture.getVideoURL())
                        .powerPointURL(fetchedLecture.getPowerPointURL())
                        .totalViews(fetchedLecture.getTotalViews())
                        .isVisible(fetchedLecture.isVisible())
                        .messageStatus("Success")
                        .build();

            } else {
                Utility.printErrorLogs("No record found for Lecture ID: " + lectureId);
                lectureResponse.setMessageStatus("Failure");
            }

            CompletableFuture.runAsync(() -> {
                processLectureReport(lectureId, studentRollNumber);
            });

            Utility.printDebugLogs("Lecture response: " + lectureResponse);
            return lectureResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error fetching lecture by ID: " + e.getMessage());
            lectureResponse.setMessageStatus("Failure");
            return lectureResponse;
        }
    }

    public LectureResponse setVisibility(long lectureId, boolean isVisible) {
        Utility.printDebugLogs("Set visibility request for lecture ID: " + lectureId);
        LectureResponse lectureResponse = new LectureResponse();

        try {
            // Validate lectureId
            if (lectureId <= 0) {
                Utility.printErrorLogs("Invalid lectureId for setting visibility");
                lectureResponse.setMessageStatus("Failure");
                return lectureResponse;
            }

            // Fetch lecture by lectureId
            Optional<Lecture> optionalLecture = lectureRepository.findById(lectureId);
            if (optionalLecture.isPresent()) {
                Lecture fetchedLecture = optionalLecture.get();

                // Set visibility
                fetchedLecture.setVisible(isVisible);
                lectureRepository.save(fetchedLecture);

                lectureResponse = LectureResponse.builder()
                        .lectureId(fetchedLecture.getLectureId())
                        .courseId(fetchedLecture.getCourseId())
                        .lectureTitle(fetchedLecture.getLectureTitle())
                        .description(fetchedLecture.getDescription())
                        .videoURL(fetchedLecture.getVideoURL())
                        .powerPointURL(fetchedLecture.getPowerPointURL())
                        .totalViews(fetchedLecture.getTotalViews())
                        .isVisible(fetchedLecture.isVisible()).publishDate(fetchedLecture.getPublishDate())
                        .messageStatus("Success")
                        .build();

                Utility.printDebugLogs("Set visibility for lecture ID: " + lectureId);
            } else {
                Utility.printErrorLogs("No record found for Lecture ID: " + lectureId);
                lectureResponse.setMessageStatus("Failure");
            }

            return lectureResponse;

        } catch (Exception e) {
            Utility.printErrorLogs("Error setting visibility for lecture: " + e.getMessage());
            lectureResponse.setMessageStatus("Failure");
            return lectureResponse;
        }
    }

    private void processLectureReport(long lectureId, String studentRollNumber) {
        try {
            Optional<LectureReport> lectureReport =
                    lectureReportRepository.findByStudentRollNumberAndLectureId(studentRollNumber, lectureId);

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String currentDateAsString = currentDate.format(formatter);

            if (!lectureReport.isPresent()) {
                LectureReport lectureReportAddition = LectureReport.builder()
                        .attempts(1)
                        .startDate(currentDateAsString)
                        .lastAccessedDate(currentDateAsString)
                        .lectureId(lectureId)
                        .studentRollNumber(studentRollNumber).build();

                lectureReportRepository.save(lectureReportAddition);
            } else {
                long attempts = lectureReport.get().getAttempts();
                lectureReport.get().setAttempts(++attempts);
                lectureReport.get().setLastAccessedDate(currentDateAsString);
                lectureReportRepository.save(lectureReport.get());
            }
        } catch (Exception e) {
            Utility.printErrorLogs("Error processing lecture report: " + e.getMessage());
        }
    }

    public LectureReportListResponse getLectureReportTableData(long lectureId) {
        LectureReportListResponse response = new LectureReportListResponse();

        try {
            // Fetch lecture reports by lectureId
            List<LectureReport> lectureReports = lectureReportRepository.findAllByLectureId(lectureId);

            // Convert to DTOs
            List<LectureReportResponse> reportResponses = lectureReports.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());


            response.setLectureReportResponseList(reportResponses);
            response.setMessageStatus("Success");

        } catch (Exception e) {
            response.setMessageStatus("Failure");
        }

        return response;
    }

    private LectureReportResponse convertToResponse(LectureReport lectureReport) {
        Optional<Student> student = studentRepository.findByRollNumber(lectureReport.getStudentRollNumber());

        if (student.isPresent()) {
            return LectureReportResponse.builder()
                    .id(lectureReport.getId())
                    .studentName(student.get().getStudentName()) // Assuming there's a method like getStudentName in your entity
                    .studentRollNumber(lectureReport.getStudentRollNumber())
                    .attempts(lectureReport.getAttempts())
                    .startDate(lectureReport.getStartDate())
                    .lastAccessedDate(lectureReport.getLastAccessedDate())
                    .build();
        }

        return null;
    }

    public LectureReportGraphResponse getLectureReportGraphData(long lectureId) {
        LectureReportGraphResponse response = new LectureReportGraphResponse();

        try {
            // Fetch lecture reports by lectureId
            List<LectureReport> lectureReports = lectureReportRepository.findAllByLectureId(lectureId);

            // Generate a map with the count of students who attempted the lecture
            Map<String, Long> studentsAttemptedMap = lectureReports.stream()
                    .map(LectureReport::getStudentRollNumber)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            // Create ranges and categorize counts into those ranges
            Map<String, Long> categorizedCounts = studentsAttemptedMap.entrySet().stream()
                    .collect(Collectors.groupingBy(entry -> getRange(entry.getKey()), Collectors.summingLong(Map.Entry::getValue)));

            // Convert the counts to String (if needed) for your graphData map
            Map<String, String> graphData = categorizedCounts.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));

            response.setGraphData(graphData);
            response.setMessageStatus("Success");

        } catch (Exception e) {
            response.setMessageStatus("Failure");
        }

        return response;
    }

    // Helper method to get the range for categorization
    private String getRange(String value) {
        // Assuming your student roll numbers are numeric and you want to categorize them in ranges
        int rangeSize = 10; // You can adjust this based on your desired range size
        int intValue = Integer.parseInt(value.replaceAll("[^\\d.]", ""));
        int startRange = (intValue / rangeSize) * rangeSize;
        int endRange = startRange + rangeSize - 1;

        return startRange + "-" + endRange;
    }
}

