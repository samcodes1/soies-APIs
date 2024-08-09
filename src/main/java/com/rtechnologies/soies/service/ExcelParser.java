package com.rtechnologies.soies.service;

import java.io.*;
import java.util.List;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

import java.util.*;


import com.opencsv.exceptions.CsvException;
import com.rtechnologies.soies.model.*;
import com.rtechnologies.soies.model.association.TeacherCourse;
import com.rtechnologies.soies.model.association.TeacherSection;
import com.rtechnologies.soies.repository.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.rtechnologies.soies.model.association.TeacherCampusSectionGradeBranch;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

@Service
public class ExcelParser {
    @Autowired
    CampusRepository campusRepositoryObj;

    @Autowired
    SectionRepository sectionRepositoryObj;

    @Autowired
    TeacherCampusSectionGradeBranchRepo TeacherCampusSectionGradeBranchRepoObj;

    @Autowired
    TeacherRepository teacherRepositoryobj;

    @Autowired
    CourseRepository courseRepository;
    @Autowired
    TeacherSectionRepository teacherSectionRepository;

    @Autowired
    TeacherCourseRepository teacherCourseRepository;

    public List<Student> parseStudentExcelFile(InputStream is) throws IOException {
        List<Student> students = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println("row data>>>>>>>>>>>>>> ");
        for (Row row : sheet) {
            System.out.println("While enter ");
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }
            Student student = new Student();
            //Roll Number	First Name	Middle Name	Last Name	Campus Name	Class Name	Section Name	Email Ids	password	
            // System.out.println("row data>>>>>>>>>>>>>> "+row.getCell(0).getStringCellValue());
            student.setRollNumber(row.getCell(0).getStringCellValue());

            String fullName = new StringBuilder().append(row.getCell(1).getStringCellValue())
                    .append(" ")
                    .append(row.getCell(2).getStringCellValue())
                    .append(" ")
                    .append(row.getCell(3).getStringCellValue()).toString();

            student.setStudentName(fullName);
            student.setCampusName(row.getCell(4).getStringCellValue());
            student.setGrade(row.getCell(5).getStringCellValue());
            student.setSectionName(row.getCell(6).getStringCellValue());
            student.setGuardianEmail(row.getCell(7).getStringCellValue());
            student.setPassword(new BCryptPasswordEncoder().encode(row.getCell(6).getStringCellValue()));
            students.add(student);
        }
        workbook.close();
        return students;
    }


    @Transactional
    public List<Teacher> parseTeacherFile(MultipartFile file) throws IOException, CsvException {
        String fileName = file.getOriginalFilename();
        List<Teacher> teachers = new ArrayList<>();

        if (fileName != null && fileName.endsWith(".csv")) {
            teachers = parseCSV(file.getInputStream());
        } else if (fileName != null && (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
            teachers = parseExcel(file.getInputStream());
        } else {
            System.err.println("Unsupported file type: " + fileName);
        }

        return teachers;
    }

    private List<Teacher> parseCSV(InputStream is) throws IOException, CsvException {
        List<Teacher> teachers = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        CSVReader csvReader = new CSVReader(reader);
        List<String[]> rows = csvReader.readAll();
        System.out.println("TEACHERS:> Number of rows in the CSV: " + rows.size());

        if (rows.size() < 2) {
            System.err.println("CSV file does not have enough data");
            return teachers;
        }

        // Extract headers
        String[] headers = rows.get(0);
        Map<String, Integer> headerIndexMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            headerIndexMap.put(headers[i].trim().toLowerCase(), i);
        }

        for (int i = 1; i < rows.size(); i++) { // Skip header row
            String[] row = rows.get(i);
            System.out.println("Processing row: " + (i + 1));

            // Ensure row has the expected number of columns
            if (row.length < headers.length) {
                System.err.println("Row " + (i + 1) + " does not have enough columns, skipping this row.");
                continue;
            }

            Teacher teacher = createTeacherFromRow(row, headerIndexMap);
            processTeacher(teacher, headerIndexMap, row);
        }
        csvReader.close();
        reader.close();
        return teachers;
    }

    private List<Teacher> parseExcel(InputStream is) throws IOException {
        List<Teacher> teachers = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        if (!rowIterator.hasNext()) {
            System.err.println("Excel file does not have enough data");
            return teachers;
        }

        Row headerRow = rowIterator.next();
        List<String> headers = new ArrayList<>();
        headerRow.forEach(cell -> headers.add(cell.getStringCellValue().trim().toLowerCase()));
        Map<String, Integer> headerIndexMap = headers.stream().collect(Collectors.toMap(h -> h, headers::indexOf));

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String[] rowData = new String[headers.size()];
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = row.getCell(i);
                rowData[i] = cell == null ? "" : cell.toString();
            }

            System.out.println("Processing row: " + (row.getRowNum() + 1));
            Teacher teacher = createTeacherFromRow(rowData, headerIndexMap);
            processTeacher(teacher, headerIndexMap, rowData);
        }

        workbook.close();
        return teachers;
    }

    private Teacher createTeacherFromRow(String[] row, Map<String, Integer> headerIndexMap) {
        Teacher teacher = new Teacher();
        teacher.setUserName(getValue(row, headerIndexMap, "username").toLowerCase());
        teacher.setPassword(new BCryptPasswordEncoder().encode(getValue(row, headerIndexMap, "password")));
        teacher.setEmployeeName(getValue(row, headerIndexMap, "firstname") + " " + getValue(row, headerIndexMap, "lastname"));
        teacher.setEmail(getValue(row, headerIndexMap, "email").toLowerCase());
        teacher.setDateOfBirth(getValue(row, headerIndexMap, "dateofbirth"));
        teacher.setGender(getValue(row, headerIndexMap, "gender"));
        teacher.setJoiningDate(getValue(row, headerIndexMap, "joiningdate"));
        teacher.setPhoneNumber(getValue(row, headerIndexMap, "phonenumber"));
        teacher.setAddress(getValue(row, headerIndexMap, "address"));
        teacher.setCampusName(getValue(row, headerIndexMap, "campus"));
        teacher.setGrade(getValue(row, headerIndexMap, "grades assigned"));
        return teacher;
    }

    private void processTeacher(Teacher teacher, Map<String, Integer> headerIndexMap, String[] row) {
        String gradesAssigned = getValue(row, headerIndexMap, "grades assigned");
        System.err.println("Grades Assigned: " + gradesAssigned);
        System.err.println("Campus Name: " + teacher.getCampusName());

        Long campusId = getOrCreateCampus(teacher.getCampusName());

        // Split grades and sections
        String[] gradesSections = gradesAssigned.split("\\),");
        for (String gradeSection : gradesSections) {
            gradeSection = gradeSection.trim();
            if (!gradeSection.endsWith(")")) {
                gradeSection += ")";
            }
            System.out.println("Processing grade-section: " + gradeSection);

            if (!gradeSection.contains("(") || !gradeSection.contains(")")) {
                System.err.println("Invalid grade-section format: " + gradeSection);
                continue;
            }
            String[] parts = gradeSection.split("\\(", 2); // Split into grade and sections
            if (parts.length < 2) {
                System.err.println("Invalid grade-section split: " + gradeSection);
                continue;
            }
            String grade = parts[0].trim();
            String sectionsPart = parts[1].replace(")", "").trim();
            String[] sections = sectionsPart.split(",");
            for (String section : sections) {
                section = section.trim();
                System.out.println("Processing section: " + section);
                try {
                    Long sectionId = getOrCreateSection(campusId, section, grade);
                    Long teacherId = getOrCreateTeacher(teacher, teacher.getEmail());

                    // Save Teacher-Campus-Section-Grade association
                    saveTeacherCampusSectionGradeAssociation(teacherId, sectionId);

                    // Process and assign courses to the teacher
                    List<Course> courses = getCoursesForGrade(grade);
                    for (Course course : courses) {
                        Long courseId = getOrCreateCourse(course.getCourseName(), grade);
                        saveTeacherCourseAssociation(teacherId, courseId);
                    }

//                     Save Teacher-Section association
//                    saveTeacherSectionAssociation(teacherId, sectionId);
                } catch (Exception e) {
                    System.err.println("Error processing section: " + section);
                    e.printStackTrace();
                }
            }
        }
    }
    private Long getOrCreateCourse(String courseName, String grade) {
        Optional<Course> courseData = courseRepository.findByCourseNameIgnoreCaseAndGrade(courseName, grade);
        Long courseId;
        if (!courseData.isPresent()) {
            Course newCourse = Course.builder().courseName(courseName).grade(grade).build();
            newCourse = courseRepository.save(newCourse);
            courseId = newCourse.getCourseId();
            System.err.println("Course does not exist, created new course with ID: " + courseId);
        } else {
            courseId = courseData.get().getCourseId();
            System.err.println("Course exists with ID: " + courseId);
        }
        return courseId;
    }

    private void saveTeacherCourseAssociation(Long teacherId, Long courseId) {
        Optional<TeacherCourse> existingAssociation = teacherCourseRepository.findByTeacherIdAndCourseId(teacherId, courseId);
        if (!existingAssociation.isPresent()) {
            teacherCourseRepository.save(TeacherCourse.builder().teacherId(teacherId).courseId(courseId).build());
            System.err.println("Added new course to teacher with ID: " + teacherId);
        } else {
            System.err.println("Teacher already has this course assigned.");
        }
    }

//    private void saveTeacherSectionAssociation(Long teacherId, String section, String grade) {
//        Optional<TeacherSection> existingAssociation = teacherSectionRepository.findByTeacherIdAndSectionAndGrade(teacherId, section, grade);
//        if (!existingAssociation.isPresent()) {
//            teacherSectionRepository.save(TeacherSection.builder().teacherId(teacherId).section(section).grade(grade).build());
//            System.err.println("Added new section to teacher with ID: " + teacherId);
//        } else {
//            System.err.println("Teacher already has this section assigned.");
//        }
//    }

    private List<Course> getCoursesForGrade(String grade) {
        // Fetch courses by grade from the repository
        return courseRepository.findCoursesByGrade(grade);
    }
    private String getValue(String[] row, Map<String, Integer> headerIndexMap, String headerName) {
        Integer index = headerIndexMap.get(headerName.toLowerCase());
        return (index != null && index < row.length) ? row[index] : "";
    }

    private Long getOrCreateCampus(String campusName) {
        Optional<Campus> campusData = campusRepositoryObj.findByCampusName(campusName);
        Long campusId;
        if (!campusData.isPresent()) {
            Campus campus = Campus.builder().campusName(campusName).build();
            campus = campusRepositoryObj.save(campus);
            campusId = campus.getId();
            System.err.println("Campus does not exist, created new campus with ID: " + campusId);
        } else {
            campusId = campusData.get().getId();
            System.err.println("Campus exists with ID: " + campusId);
        }
        return campusId;
    }

    private Long getOrCreateSection(Long campusId, String sectionName, String grade) {
        Optional<Section> sectionData = sectionRepositoryObj.findByCampusIdAndSectionNameIgnoreCaseAndGrade(campusId, sectionName, grade);
        Long sectionId;
        if (!sectionData.isPresent()) {
            Section newSection = Section.builder().campusId(campusId).grade(grade).sectionName(sectionName).build();
            newSection = sectionRepositoryObj.save(newSection);
            sectionId = newSection.getId();
            System.err.println("Section does not exist, created new section with ID: " + sectionId);
        } else {
            sectionId = sectionData.get().getId();
            System.err.println("Section exists with ID: " + sectionId);
        }
        return sectionId;
    }

    private Long getOrCreateTeacher(Teacher teacher, String email) {
        Optional<Teacher> teacherData = teacherRepositoryobj.findByEmail(email);
        Long teacherId;
        if (!teacherData.isPresent()) {
            Teacher newTeacher = teacherRepositoryobj.save(teacher);
            teacherId = newTeacher.getTeacherId();
            System.err.println("Teacher does not exist, created new teacher with ID: " + teacherId);
        } else {
            teacherId = teacherData.get().getTeacherId();
            System.err.println("Teacher already exists with ID: " + teacherId);
        }
        return teacherId;
    }

    private void saveTeacherCampusSectionGradeAssociation(Long teacherId, Long sectionId) {
        Optional<TeacherCampusSectionGradeBranch> existingAssociation = TeacherCampusSectionGradeBranchRepoObj.findByTeacheIdFkAndSectionIdFk(teacherId, sectionId);
        if (!existingAssociation.isPresent()) {
            TeacherCampusSectionGradeBranchRepoObj.save(TeacherCampusSectionGradeBranch.builder().teacheIdFk(teacherId).sectionIdFk(sectionId).build());
            System.err.println("Added new section to teacher with ID: " + teacherId);
        } else {
            System.err.println("Teacher already has this section assigned.");
        }
    }

    public List<Student> csvParserStudent(MultipartFile file) {
        List<Student> students = new ArrayList<>();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No Record Found in csv");
        }

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            // Create a CSVReader using OpenCSV
            CSVReader csvReader = new CSVReader(reader);
            // Skip the header row
            csvReader.skip(1);
            // Read the CSV data line by line
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (nextRecord.length == 0) {
                    continue;
                }
                // Check if the first field is empty (This may indicate the end of the file)
                if (nextRecord[0].isEmpty()) {
                    break;
                }
                Student student = new Student();
                System.out.println("Record: " + Arrays.toString(nextRecord));
                student.setRollNumber(nextRecord[0].trim());
                String fullName = new StringBuilder().append(nextRecord[1])
                        .append(" ")
                        .append(nextRecord[2])
                        .append(" ")
                        .append(nextRecord[3]).toString();

                student.setStudentName(fullName.trim().toLowerCase());
                student.setCampusName(nextRecord[4].trim().toLowerCase());
                student.setGrade(nextRecord[5].trim().toLowerCase());
                student.setSectionName(nextRecord[6].trim().toLowerCase());
                student.setGuardianEmail(nextRecord[7].trim().toLowerCase());
                student.setPassword(new BCryptPasswordEncoder().encode(nextRecord[8].trim()));
                students.add(student);
            }
            System.out.println("FILE READ COMPLETE RETURNING");
            return students;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("parsing error");
        }
    }

    public List<Teacher> csvParserTeacher(MultipartFile file) {
        List<Teacher> teachers = new ArrayList<>();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No Record Found in csv");
        }

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            // Create a CSVReader using OpenCSV
            CSVReader csvReader = new CSVReader(reader);
            // Skip the header row
            csvReader.skip(1);
            // Read the CSV data line by line
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (nextRecord.length == 0) {
                    continue;
                }
                // Check if the first field is empty (This may indicate the end of the file)
                if (nextRecord[0].isEmpty()) {
                    break;
                }
                Teacher teacher = new Teacher();
                System.out.println("Record: " + Arrays.toString(nextRecord));
                teacher.setUserName(nextRecord[1].trim());
                teacher.setPassword(new BCryptPasswordEncoder().encode(nextRecord[2].trim()));
                teacher.setEmployeeName(nextRecord[3].trim().toLowerCase() + " " + nextRecord[4].trim().toLowerCase());
                teacher.setEmail(nextRecord[5].trim().toLowerCase());
                teacher.setGrade(nextRecord[6].trim().toLowerCase());
                teacher.setCampusName(nextRecord[7].trim().toLowerCase());
                teachers.add(teacher);
            }
            System.out.println("FILE READ COMPLETE RETURNING");
            return teachers;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("parsing error");
        }
    }
}
